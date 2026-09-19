package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MigRepository
import com.example.model.AvatarAssets
import com.example.model.AvatarConfig
import com.example.model.MigPresetAvatar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AvatarViewModel(
    private val repository: MigRepository
) : ViewModel() {

    val currentConfig: StateFlow<AvatarConfig> = repository.userProfile
        .map { it.avatarConfig }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AvatarConfig()
        )

    private val _selectedPreset = MutableStateFlow<MigPresetAvatar?>(null)
    val selectedPreset: StateFlow<MigPresetAvatar?> = _selectedPreset.asStateFlow()

    fun applyPreset(preset: MigPresetAvatar) {
        _selectedPreset.value = preset
        repository.updateAvatarConfig(
            preset.config.copy(
                presetId = preset.id,
                customImageRes = preset.drawableRes
            )
        )
    }

    fun equipPreset(presetId: String, drawableRes: Int, config: AvatarConfig) {
        repository.updateAvatarConfig(config.copy(presetId = presetId, customImageRes = drawableRes))
    }

    fun saveAvatar() {
        // Already persisted to Room DB
    }

    fun saveConfig(config: AvatarConfig) {
        repository.updateAvatarConfig(config)
    }
}
