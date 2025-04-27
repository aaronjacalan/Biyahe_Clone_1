package com.android.biyahe.data

import android.os.Parcelable
import com.android.biyahe.R
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Route (
    var code : String = "",
    var profile : Int = R.drawable.ic_launcher_foreground,
    var destinations_to : List<Destination>,
    var destinations_back : List<Destination>,
    var routeTo_resource : Int = R.drawable.ic_launcher_foreground,
    var routeBack_resource : Int = R.drawable.ic_launcher_foreground
) : Parcelable