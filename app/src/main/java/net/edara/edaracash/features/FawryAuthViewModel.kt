package net.edara.edaracash.features

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.edara.edaracash.models.Consts
import javax.inject.Inject

@HiltViewModel
class FawryAuthViewModel @Inject constructor(private val dataStore: DataStore<Preferences>) :
    ViewModel() {
    fun saveFawryAuth(userName: String, password: String) {
        viewModelScope.launch {
            dataStore.edit {
                it[Consts.FAWRY_USERNAME] = userName
                it[Consts.FAWRY_Password] = password
            }
        }
    }
}