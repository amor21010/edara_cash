package net.edara.edaracash.features.invoice

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.edara.domain.models.print.PrintResponse
import net.edara.domain.use_case.PrintUseCase
import net.edara.edaracash.models.Consts
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val printUseCase: PrintUseCase,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _unitInfo = MutableStateFlow<ResultState>(
        ResultState.Init
    )
    val unitInfo = _unitInfo

    fun getUnitInfo(servicesId: List<String?>) {
        _unitInfo.value = ResultState.Loading
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                val token = preferences[Consts.USER_TOKEN]
                try {
                    val result = printUseCase(
                        servicesId, "bearer $token"
                    )

                    if (result.data != null) _unitInfo.value =
                        ResultState.Success(result.data)
                    else result.failures?.requestIdentifiers?.forEach {
                        _unitInfo.value =
                            ResultState.Error(it)
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
        }
    }
}

sealed class ResultState {
    object Init : ResultState()
    object Loading : ResultState()
    class Success(val unitInfo: PrintResponse.Data?) : ResultState()
    class Error(val msg: String?) : ResultState()
    object Unauthorized : ResultState()
}
