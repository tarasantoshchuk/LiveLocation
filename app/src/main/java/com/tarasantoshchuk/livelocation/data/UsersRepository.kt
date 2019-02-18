package com.tarasantoshchuk.livelocation.data

import com.tarasantoshchuk.livelocation.model.Location
import com.tarasantoshchuk.livelocation.model.User
import io.reactivex.Single
import java.util.*

class UsersRepository {
    fun getUsers(): Single<List<User>> {
        return Single.defer {
            Single.just(
                Arrays.asList<User>(
                    User("Name1", "1"),
                    User("Mame1", "2"),
                    User("Bame1", "3"),
                    User("#ame1", "4")
                )
            )
        }
    }

    fun getUserLocations(): Single<Map<User, Location>> {
        return Single.defer {
            Single.just(
                mapOf(
                    User("Name1", "1") to Location(50.0, 50.0),
                    User("Mame1", "2") to Location(150.0, 90.0),
                    User("Bame1", "3") to Location(50.0, 90.0),
                    User("#ame1", "4") to Location(150.0, 50.0)
                )
            )
        }
    }
}