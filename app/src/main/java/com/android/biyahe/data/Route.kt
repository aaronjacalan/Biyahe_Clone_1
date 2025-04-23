package com.android.biyahe.data

import android.os.Parcelable
import com.android.biyahe.R
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Route (
    var jeepney_code : String = "",
    var destination : String = "",
    var photoResource : Int = R.drawable.ic_launcher_foreground,
    var destinations : List<Destination>
) : Parcelable