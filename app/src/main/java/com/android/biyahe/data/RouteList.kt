package com.android.biyahe.data

import com.android.biyahe.R

object RouteDataManager {
    val routelist = mutableListOf(
            Route("09C", "Basak - Colon", R.drawable.profile_09c),
            Route("10H", "Bulacao - SM", R.drawable.profile_10h),
            Route("23D", "Parkmall - Opon", R.drawable.profile_23d),
            Route("03Q", "Ayala - SM", R.drawable.profile_03q)
    )

    val bookmarked = mutableListOf<Route>()
}