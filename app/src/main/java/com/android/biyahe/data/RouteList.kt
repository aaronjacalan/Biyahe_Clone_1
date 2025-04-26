package com.android.biyahe.data

import com.android.biyahe.R

object RouteDataManager {
    val routelist = mutableListOf(
            Route("03Q", "Ayala - SM", R.drawable.profile_03q,
                listOf(
                    Destination("Ayala Center Cebu"),
                    Destination("Landers Superstore Cebu"),
                    Destination("Juan Luna Avenue"),
                    Destination("SM City Cebu")
                )),

            Route("09C", "Basak - Colon", R.drawable.profile_09c,
                listOf(
                    Destination("Quiot"),
                    Destination("Southwestern University Basak Campus"),
                    Destination("Don Vicente Rama Memorial National High School"),
                    Destination("Shopwise"),
                    Destination("Mambaling"),
                    Destination("Cebu Institute of Technology University (CITU)"),
                    Destination("Salazar Colleges"),
                    Destination("Cebu City Medical Center"),
                    Destination("Cebu South Bus Terminal (CSBT)"),
                    Destination("University of San Jose Recoletos"),
                    Destination("Sikatuna St"),
                    Destination("Colon Obelisk")
                ),
                R.drawable.route_09c),

            Route("10H", "Bulacao - SM", R.drawable.profile_10h,
                listOf(
                    Destination("Bulacao"),
                    Destination("Pardo"),
                    Destination("University of San Jose Recoletos"),
                    Destination("Shopwise"),
                    Destination("Mambaling Flyover"),
                    Destination("Cebu Institute of Technology University (CITU)"),
                    Destination("Salazar Colleges"),
                    Destination("Cebu City Medical Center"),
                    Destination("Cebu South Bus Terminal (CSBT)"),
                    Destination("University of San Jose Recoletos"),
                    Destination("Tiburcio Padilla Street"),
                    Destination("B Benedicto Street"),
                    Destination("General Maxilom Avenue Extension"),
                    Destination("White Gold House"),
                    Destination("A Soriano Avenue"),
                    Destination("SM City Cebu")
                )),

            Route("23D", "Parkmall - Opon", R.drawable.profile_23d,
                listOf(
                    Destination("Parkmall"),
                    Destination("S&R Membership Shop"),
                    Destination("Cebu Doctors University"),
                    Destination("St. James Amusement Park"),
                    Destination("Mandaue City Sports and Cultural Complex"),
                    Destination("Bureau of Internal Revenue (BIR)"),
                    Destination("New Mandaue Public Market"),
                    Destination("Mandaue City Hospital"),
                    Destination("Norkis Park"),
                    Destination("Mandaue City Central School"),
                    Destination("University of Visayas"),
                    Destination("Hotel Nenita"),
                    Destination("University of Cebu"),
                    Destination("Mantawe Road"),
                    Destination("Ompad Street"),
                    Destination("La Nueva Supermarket"),
                    Destination("Super Metro Gaisano Lapu-Lapu"),
                    Destination("Lapu Lapu City PUJ Terminal")
                ))
    )

    val bookmarked = mutableListOf<Route>()
}