package com.tarasantoshchuk.livelocation.model

import java.util.*

data class User(val name: String, val id: String)

data class Location(val lat: Double, val lng: Double, val lastUpdated: Date = Date())