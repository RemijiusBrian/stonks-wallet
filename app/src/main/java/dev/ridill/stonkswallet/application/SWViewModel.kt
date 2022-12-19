package dev.ridill.stonkswallet.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.stonkswallet.core.data.preferences.PreferencesManager
import javax.inject.Inject

@HiltViewModel
class SWViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {
    val preferences = preferencesManager.preferences.asLiveData()
}