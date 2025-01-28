package com.forrestgump.ig.ui.screens.notification

import com.forrestgump.ig.remote.models.AppUser
import com.forrestgump.ig.utils.models.Post

data class UiState(
    var profileImage: String = "",
    var username: String = "",
    var isLoading: Boolean = false
)
