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
import net.edara.edaracash.features.util.JWTUtils
import net.edara.edaracash.models.Consts.USER_TOKEN
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(private val dataStore: DataStore<Preferences>) :
    ViewModel() {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    init {
        getStoredToken()
    }

    private fun getStoredToken() {
        viewModelScope.launch {

            dataStore.data.collect {
                val token = it[USER_TOKEN]
                if (token != null) {
                    val user = JWTUtils.getUserJWT(token)
                    if (user != null) {
                        if (user.Role?.lowercase(Locale.ROOT) != "technician") {
                            _token.value = token
                        } else removeToken()
                    }
                } else _token.value = ""


            }
        }
    }

    private suspend fun removeToken() {
        _token.value = ""
        dataStore.edit {
            it.remove(USER_TOKEN)
        }
        getStoredToken()
    }
}