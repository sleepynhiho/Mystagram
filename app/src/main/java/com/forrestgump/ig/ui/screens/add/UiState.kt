package com.forrestgump.ig.ui.screens.add

import com.forrestgump.ig.utils.models.Media

data class UiState(
    var mediaList: List<Media> = emptyList(),
    var selectedMedia: Media? = null,
    var caption: String = ""
)
