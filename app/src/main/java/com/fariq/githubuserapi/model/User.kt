package com.fariq.githubuserapi.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        var avatar : String? = "",
        var username : String? = "",
        var name : String? = "",
        var company : String? = "",
        var location : String? = "",
        var followers : Int? = 0,
        var following : Int? = 0
) : Parcelable