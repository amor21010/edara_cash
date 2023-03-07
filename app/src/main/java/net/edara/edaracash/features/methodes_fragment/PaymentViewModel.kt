package net.edara.edaracash.features.methodes_fragment

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.edara.domain.models.payment.PaymentRequest
import net.edara.domain.use_case.PaymentUseCase
import net.edara.edaracash.models.Consts.USER_TOKEN
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentUseCase: PaymentUseCase,
    private val dataStore: DataStore<Preferences>
) :
    ViewModel() {
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Init)
    val paymentState: StateFlow<PaymentState> = _paymentState


     fun makePayment(paymentRequest: PaymentRequest) {
         viewModelScope.launch {
             dataStore.data.collect {
                 _paymentState.value = PaymentState.Loading
                 val token = it[USER_TOKEN]
                 if (token == null) _paymentState.value = PaymentState.Unauthorized
                 else {
                     try {
                         val response = paymentUseCase.invoke(paymentRequest, token)
                         if (response.data != null && response.data!!) {
                             _paymentState.value = PaymentState.Succeeded

                         } else {
                             _paymentState.value = PaymentState.Failed(response.message)

                         }

                     } catch (e: Exception) {
                         if (e.message.toString().uppercase().contains("UNAUTHORIZED")) {
                             _paymentState.value = PaymentState.Unauthorized

                         }
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