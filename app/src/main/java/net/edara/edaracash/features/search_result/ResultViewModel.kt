package net.edara.edaracash.features.search_result

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.edara.domain.models.getAllService.GetAllServiceResonse.Data.Service
import net.edara.domain.models.print.PrintResponse
import net.edara.domain.use_case.InsuranceServicePrintUseCase
import net.edara.domain.use_case.PrivetServicePrintUseCase
import net.edara.edaracash.models.Consts.USER_TOKEN
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val privetServicePrintUseCase: PrivetServicePrintUseCase,
    private val insuranceServicePrintUseCase: InsuranceServicePrintUseCase,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _unitInfo = MutableStateFlow<ResultState>(ResultState.Init)
    val unitInfo = _unitInfo

    fun getUnitInfo(servicesId: List<String?>, isInsurance: Boolean) {
        _unitInfo.value = ResultState.Loading
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                val token = preferences[USER_TOKEN]
                try {
                    val result = if (!isInsurance) privetServicePrintUseCase(
                       servicesId, "bearer $token"
                    ) else insuranceServicePrintUseCase(servicesId, "bearer $token")

                    if (result.data != null) _unitInfo.value = ResultState.Success(result.data)
                    else result.failures?.requestIdentifiers?.forEach {
                        _unitInfo.value = ResultState.Error(it)
                    }
                } catch (e: Exception) {
                    if (e.message?.uppercase().toString()
                            .contains("UNAUTHORIZED")
                    ) _unitInfo.value = ResultState.Unauthorized
                    else _unitInfo.value = ResultState.Error(e.message.toString())

                }

            }
        }
    }



    private val _selectedServices = MutableLiveData<List<Service>>(
        mutableListOf()
    )
    val selectedServices: LiveData<List<Service>>
        get() = _selectedServices


    fun addServiceToSelection(services: Service) {
        if (!_selectedServices.value!!.contains(services)) _selectedServices.value =
            _selectedServices.value!! + services
    }

    fun removeOrderFromSelection(services: Service) {
        if (_selectedServices.value!!.contains(services))

            _selectedServices.value = _selectedServices.value!! - services


    }



}

sealed class ResultState {
    object Init : ResultState()
    object Loading : ResultState()
    class Success(val unitInfo: PrintResponse.Data?) : ResultState()
    class Error(val msg: String?) : ResultState()
    object Unauthorized : ResultState()
}

