package com.android.biyahe.data

import android.os.Parcelable
import com.android.biyahe.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Destination (
    var title : String = "",
    var type : String = ""
) : Parcelable