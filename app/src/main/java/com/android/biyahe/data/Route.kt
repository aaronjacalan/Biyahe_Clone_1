package com.android.biyahe.data

import android.os.Parcelable
import com.android.biyahe.R
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Route (
    var code : String = "",
    var summary : String = "",
    var photo_resource : Int = R.drawable.ic_launcher_foreground,
    var destinations : List<Destination>
) : Parcelable