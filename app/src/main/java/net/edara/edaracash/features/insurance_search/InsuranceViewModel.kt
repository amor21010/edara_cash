package net.edara.edaracash.features.insurance_search

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.domain.models.getAllService.GetAllServicesRequestDto
import net.edara.domain.use_case.GetAllInsuranceServicesUseCase
import net.edara.edaracash.features.service_search.SearchState
import net.edara.edaracash.models.Consts
import javax.inject.Inject

@HiltViewModel
class InsuranceViewModel @Inject constructor(
    private val getAllInsuranceServicesUseCase: GetAllInsuranceServicesUseCase,
    private val dataStore: DataStore<Preferences>
):ViewModel() {
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Init)
    val service: StateFlow<SearchState> = _searchState

    fun getAllService(unitNumber: String?, analysisCode: String?) {
        _searchState.value = SearchState.Loading
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                val token = preferences[Consts.USER_TOKEN]
                try {
                    val result = getAllInsuranceServicesUseCase(
                        GetAllServicesRequestDto(
                            filter = GetAllServicesRequestDto.Filter(
                                analysisCode = analysisCode, unitNo = unitNumber
                            ),
                            sorting = GetAllServicesRequestDto.Sorting(),
                            paginator = GetAllServicesRequestDto.Paginator()
                        ), "bearer $token"
                    )

                    if (result.result?.key != 1) handlePossibleErrors(result)
                    else {
                        val serviceList = result.data!!.services
                        if (!serviceList.isNullOrEmpty()) {
                            _searchState.value = SearchState.Success(serviceList)
                        }
                    }

                } catch (e: Exception) {
                    if (e.message?.uppercase().toString()
                            .contains("UNAUTHORIZED")
                    ) _searchState.value = SearchState.UnAuthorized
                }
            }
        }
    }

    private fun handlePossibleErrors(result: GetAllServiceResonse) {
        _searchState.value = SearchState.Failed(result.message)


        if (result.failures != null) {
            result.failures!!.filterAnalysisCode?.forEach {
                _searchState.value = SearchState.Failed(it)
            }
            result.failures!!.filterUnitNo?.forEach {
                _searchState.value = SearchState.Failed(it)
            }
        }
    }

    fun resetState() {
        _searchState.value = SearchState.Init
    }

}