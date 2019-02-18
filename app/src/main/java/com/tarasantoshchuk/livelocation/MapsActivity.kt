package com.tarasantoshchuk.livelocation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tarasantoshchuk.livelocation.data.UsersRepository
import com.tarasantoshchuk.livelocation.model.Location
import com.tarasantoshchuk.livelocation.model.User
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private val data = BehaviorSubject.create<Map<User, Location>>()

    private val usersRepository = UsersRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        usersRepository
            .getUserLocations()
            .repeatWhen {
                it.delay(5, TimeUnit.MINUTES)
            }
            .doOnNext(data::onNext)
            .doOnNext { mapFragment.getMapAsync(this::onMapReady) }
            .subscribe()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.clear()

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        data.value?.let {
            it.forEach { (user, location) ->
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(location.lat, location.lng))
                        .title(user.name)
                )
            }
        }
    }
}
