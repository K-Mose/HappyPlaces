package com.example.happyplaces.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityMapBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityMapBinding

    private var mHappyPlaceDetail: HappyPlaceModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            mHappyPlaceDetail = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }
        binding.apply {
            mHappyPlaceDetail?.also {
                setSupportActionBar(toolbarMap)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.title = it.title
                toolbarMap.setNavigationOnClickListener { onBackPressed() }
            }

            val supportMapFragment: SupportMapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            supportMapFragment.getMapAsync(this@MapActivity)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        // 맵이 준비되었을 때, 마커 설정
        val position = LatLng(mHappyPlaceDetail!!.latitude, mHappyPlaceDetail!!.longitude)
        googleMap!!
                .addMarker(
                        MarkerOptions()
                                .position(position)
                                .title(mHappyPlaceDetail!!.title)
                )
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 12f)
        googleMap!!.animateCamera(newLatLngZoom)
    }
}