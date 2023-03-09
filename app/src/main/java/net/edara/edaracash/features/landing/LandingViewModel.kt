package net.edara.edaracash.features.landing

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.edara.domain.use_case.ProfileUseCase
import net.edara.edaracash.features.util.TokenUtils
import net.edara.edaracash.models.Consts
import net.edara.edaracash.models.Consts.USER_TOKEN
import net.edara.edaracash.models.UserState
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase, private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _userState = MutableStateFlow<UserState>(UserState.Init)
    val userState: StateFlow<UserState> = _userState

    init {
        getStoredToken()
    }

    private fun getStoredToken() {

        viewModelScope.launch {

            dataStore.data.collect {
                val token = it[USER_TOKEN]
                if (token != null) {
                    val user = TokenUtils.getUserJWT(token)
                    if (user.role == Consts.ALLOWED_ROLE) {
                        getUserProfile(token)

                    } else {
                        _userState.value = UserState.NotAllowed
                        removeToken()
                    }
                } else _userState.value = UserState.UnAuthorized


            }
        }
    }

    private suspend fun removeToken() {

        dataStore.edit {
            it.remove(USER_TOKEN)
        }
        getStoredToken()
    }

    private suspend fun getUserProfile(token: String) {
        try {
            val result = profileUseCase.invoke(token)
            if (result.data != null) _userState.value = UserState.Success(result.data!!,token)
        } catch (e: Exception) {
            if (e.message?.uppercase().toString()
                    .contains("UNAUTHORIZED")
            ) _userState.value = UserState.TokenExpired
            else _userState.value = UserState.Failure(e.message.toString())
        }
    }
}
