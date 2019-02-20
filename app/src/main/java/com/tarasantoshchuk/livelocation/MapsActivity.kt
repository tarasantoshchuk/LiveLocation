package com.tarasantoshchuk.livelocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private val data = BehaviorSubject.create<List<Pair<User, Location>>>()

    private val usersRepository = UsersRepository()

    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        usersRepository
            .getUserLocations()
            .repeatWhen {
                it.delay(5, TimeUnit.MINUTES)
            }
            .doOnNext(data::onNext)
            .doOnNext { mapFragment.getMapAsync(this::onMapReady) }
            .subscribe()

        setupUsersRows()
    }

    private fun setupUsersRows() {
        findViewById<RecyclerView>(R.id.users_names).apply {
            layoutManager = LinearLayoutManager(this@MapsActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                    return object : RecyclerView.ViewHolder(
                        LayoutInflater.from(this@MapsActivity).inflate(
                            R.layout.user_item,
                            parent,
                            false
                        )
                    ) {}
                }

                override fun getItemCount(): Int {
                    return data.value?.size ?: 0
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    holder.itemView.findViewById<TextView>(R.id.user_text)
                    holder.itemView.setOnClickListener {
                        selectedId = data.value?.get(position)?.first?.id
                        mapFragment.getMapAsync(this@MapsActivity::onMapReady)
                    }
                }

            }
        }
    }

    private var selectedId: String? = null

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

        data.value?.let {
            it.forEach { (user, location) ->
                map.addMarker(
                    MarkerOptions()
                        .position(toLatLng(location))
                        .title(user.name)
                )

                if (user.id == selectedId) {
                    map.moveCamera(CameraUpdateFactory.newLatLng(toLatLng(location)))
                }
            }
        }
    }

    private fun toLatLng(location: Location) = LatLng(location.lat, location.lng)
}
