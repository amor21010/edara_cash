package net.edara.edaracash.features.invoice

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import net.edara.domain.models.print.PrintResponse
import net.edara.domain.use_case.InsuranceServicePrintUseCase
import net.edara.domain.use_case.PrivetServicePrintUseCase
import net.edara.edaracash.features.util.TokenUtils
import net.edara.edaracash.models.Consts
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val privetServicePrintUseCase: PrivetServicePrintUseCase,
    private val insuranceServicePrintUseCase: InsuranceServicePrintUseCase,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _unitInfo = MutableStateFlow<ResultState>(
        ResultState.Init
    )
    val unitInfo = _unitInfo
    private val _permissions = MutableStateFlow<List<String?>?>(
        listOf("")
    )
    val permissions = _permissions






    suspend fun getUnitInfo(servicesId: List<String?>, isInsurance: Boolean) {
        _unitInfo.value = ResultState.Loading


        try {
            dataStore.data.collect { preferences ->
                val token = preferences[Consts.USER_TOKEN].toString()
                _permissions.value = TokenUtils.getUserJWT(token).permissions
                val result = if (isInsurance) insuranceServicePrintUseCase(
                    servicesId, "bearer $token"
                ) else
                    privetServicePrintUseCase(
                        servicesId, "bearer $token"
                    )
                if (result.data != null) {
                    _unitInfo.value =
                        ResultState.Success(result.data)
                    dataStore.edit { it[Consts.IS_PAYMENT_FIRST_TIME] = true }
                } else result.failures?.requestIdentifiers?.forEach {
                    _unitInfo.value =
                        ResultState.Error(it)
                }
            }

        } catch (e: Exception) {

            if (e.message?.uppercase().toString()
                    .contains("UNAUTHORIZED")
            ) _unitInfo.value =
                ResultState.Unauthorized
            else _unitInfo.value =
                ResultState.Error(e.message.toString())


        }


    }

    fun removeInvoice() {
        _unitInfo.value = ResultState.Init
    }
}

sealed class ResultState {
    object Init : ResultState()

    object Loading : ResultState()
    class Success(val unitInfo: PrintResponse.Data?) : ResultState()
    class Error(val msg: String?) : ResultState()


    object Unauthorized : ResultState()
}
