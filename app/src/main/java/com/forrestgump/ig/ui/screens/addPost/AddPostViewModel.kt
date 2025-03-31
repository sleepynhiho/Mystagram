package com.forrestgump.ig.ui.screens.addPost

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
) : ViewModel() {
    var uiState = MutableStateFlow(UiState())
        private set
    var selectedImages = MutableStateFlow<List<Uri>>(emptyList())
        private set

    // Hàm cập nhật danh sách ảnh
    fun updateSelectedImages(newImages: List<Uri>) {
        selectedImages.value = newImages
    }

    private fun clearUiState() {
        uiState.update { UiState() }
    }

    override fun onCleared() {
        super.onCleared()
        clearUiState()
    }
}
