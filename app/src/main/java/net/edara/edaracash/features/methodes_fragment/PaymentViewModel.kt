package net.edara.edaracash.features.methodes_fragment

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.edara.domain.models.payment.PaymentRequest
import net.edara.domain.models.print.PrintResponse
import net.edara.domain.use_case.InsuranceServicePaymentUseCase
import net.edara.domain.use_case.InsuranceServicePrintUseCase
import net.edara.domain.use_case.PrivetServicePaymentUseCase
import net.edara.domain.use_case.PrivetServicePrintUseCase
import net.edara.edaracash.models.Consts.USER_TOKEN
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val privetServicePaymentUseCase: PrivetServicePaymentUseCase,
    private val insuranceServicePaymentUseCase: InsuranceServicePaymentUseCase,
    private val insuranceServicePrintUseCase: InsuranceServicePrintUseCase,
    private val dataStore: DataStore<Preferences>,
    private val privetServicePrintUseCase: PrivetServicePrintUseCase
) :
    ViewModel() {
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Init)
    val paymentState: StateFlow<PaymentState> = _paymentState
    private val _unitInfo = MutableStateFlow<ResultState>(
        ResultState.Init
    )
    val unitInfo = _unitInfo

    suspend fun getUnitInfo(
        servicesId: List<String?>,
        updating: Boolean = false,
        isInsurance: Boolean
    ) {
        _unitInfo.value = ResultState.Loading
        dataStore.data.collect { preferences ->
            val token = preferences[USER_TOKEN]
            try {
                val result = if (!isInsurance) privetServicePrintUseCase(
                    servicesId, "bearer $token"
                ) else insuranceServicePrintUseCase(servicesId, "bearer $token")

                if (result.data != null)
                    _unitInfo.value =
                        if (!updating) ResultState.Success(result.data)
                        else ResultState.Updated(result.data)
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

    fun makePayment(paymentRequest: PaymentRequest, isInsurance: Boolean) {


        viewModelScope.launch {
            dataStore.data.collect {

                _paymentState.value = PaymentState.Loading
                val token = it[USER_TOKEN]
                if (token == null) {
                    _paymentState.value = PaymentState.Unauthorized
                    return@collect
                }
                try {
                    val response = if (isInsurance)insuranceServicePaymentUseCase.invoke(
                        paymentRequest, token
                    ) else privetServicePaymentUseCase.invoke(paymentRequest, token)
                    if (response.data != null && response.data!!) {
                        _paymentState.value = PaymentState.Succeeded
                        paymentRequest.requestIdentifers?.let { it1 ->
                            getUnitInfo(
                                servicesId = it1, updating = true, isInsurance
                            )
                            }
                        //  it[IS_PAYMENT_FIRST_TIME] = false
                        } else {
                        _paymentState.value =
                            PaymentState.Failed("${response.message} ,${response.failures}")

                    }

                    } catch (e: Exception) {
                    if (e.message.toString().uppercase().contains("UNAUTHORIZED")) {
                        _paymentState.value = PaymentState.Unauthorized

                    }


                }
                }


        }

    }




    sealed class PaymentState {
        object Init : PaymentState()
        object Loading : PaymentState()
        object Succeeded : PaymentState()
        object Unauthorized : PaymentState()
        class Failed(val msg: String?) : PaymentState()
    }
}


sealed class ResultState {
    object Init : ResultState()
    object Loading : ResultState()
    class Success(val unitInfo: PrintResponse.Data?) : ResultState()
    class Updated(val unitInfo: PrintResponse.Data?) : ResultState()
    class Error(val msg: String?) : ResultState()
    object Unauthorized : ResultState()
}
