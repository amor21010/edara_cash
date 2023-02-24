package net.edara.edaracash.features.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.edara.domain.models.login.LoginResponse
import net.edara.domain.use_case.LoginUseCase
import net.edara.edaracash.features.util.JWTUtils
import net.edara.edaracash.models.Consts.USER_TOKEN
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase, private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginResponse?>(null)
    val loginState: StateFlow<LoginResponse?> = _loginState
    private val _errorState = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _errorState

    suspend fun login(userName: String, password: String) {
        try {
            val result = loginUseCase(userName, password)
            val token = result.data?.token
            if (token != null) {
                val user = JWTUtils.getUserJWT(result.data?.token!!)
                if (user != null && user.Role?.lowercase(Locale.ROOT) == "technician") {
                    _errorState.value =
                        "This User is not Allowed For This Application"
                } else {
                    _errorState.value = null
                    dataStore.edit {
                        it[USER_TOKEN] = result.data?.token!!
                    }
                    _loginState.value = result
                }
            } else {
                _errorState.value = result.message
            }
        } catch (e: Exception) {
            _errorState.value = e.message
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it.remove(USER_TOKEN)
        }
    }
}