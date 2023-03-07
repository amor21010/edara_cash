package net.edara.edaracash.features.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.edara.domain.use_case.LoginUseCase
import net.edara.domain.use_case.ProfileUseCase
import net.edara.edaracash.features.util.TokenUtils
import net.edara.edaracash.models.Consts.USER_TOKEN
import net.edara.edaracash.models.UserState
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,private val profileUseCase: ProfileUseCase, private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _loginState = MutableStateFlow<UserState>(UserState.Init)
    val loginState: StateFlow<UserState> = _loginState

    suspend fun login(userName: String, password: String) {
        try {
            val result = loginUseCase(userName, password)
            val token = result.data?.token
            if (token != null) {
                val user = TokenUtils.getUserJWT(result.data?.token!!)
                if (user != null && user.role?.lowercase(Locale.ROOT) == "pos-technician") {
                    _loginState.value =
                        UserState.Failure("This User is not Allowed For This Application")
                } else {

                    dataStore.edit {
                        it[USER_TOKEN] = token
                    }
                    getUserProfile(token)
                }
            } else {
                _loginState.value = UserState.Failure(result.message!!)
            }
        } catch (e: Exception) {
            _loginState.value = UserState.Failure(e.message!!)

        }
    }

    private suspend fun getUserProfile(token: String) {
        try {
            val result = profileUseCase.invoke(token)
            if (result.data != null) _loginState.value = UserState.Success(result.data!!,token)
        } catch (e: Exception) {
            if (e.message?.uppercase().toString()
                    .contains("UNAUTHORIZED")
            ) _loginState.value = UserState.TokenExpired
            else _loginState.value = UserState.Failure(e.message.toString())
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it.remove(USER_TOKEN)
        }
    }
}