package com.forrestgump.ig.ui.screens.auth

import android.net.Uri

data class UiState(
    var emailOrUsername: String = "",
    var email: String = "",
    var username: String = "",
    var password: String = "",
    var profileImage: Uri? = null,
    var showDialog: Boolean = false,
    var isLoading: Boolean = false,
    var errorTitle: String = "",
    var errorSubTitle: String = "",
    var errorOrSuccess: String = "",
    var errorOrSuccessEmail: String = "",
    var errorOrSuccessUsername: String = ""
)
