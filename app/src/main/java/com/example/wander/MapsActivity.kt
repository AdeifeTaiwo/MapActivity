package com.example.wander

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val TAG = MapsActivity::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1
    val homelatlng = LatLng(7.48727572549505, 4.533131903751711)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)


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

        val latitude = 7.485622
        val longitude = 4.531817


        val location3 = LatLng(7.485622,4.531817)
        googleMap.addMarker(MarkerOptions().position(location3).title("Bangalore"))

        val homeLatLng = LatLng(latitude, longitude)
        val zoomLevel = 10f
        map.addMarker(MarkerOptions().title("Flozzy Cuisine").position(homeLatLng))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
        //setMapLongClick(map)
        setPoiClick(map)
        setMapStyle(map)
        enableMyLocation(map)
        // Add a marker in Sydney and move the camera
       // val sydney = LatLng(-34.0, 151.0)
        //map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //map.moveCamera(CameraUpdateFactory.newLatLng(sydney))



    }











    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /**private fun setMapLongClick(map:GoogleMap){

        map.setOnMapClickListener { latlng ->
            val snippet = String.format(
                    Locale.getDefault(),
                    "Lat: %1$.5f, Long: %2$.5f",
                    latlng.latitude,
                    latlng.longitude
            )
            map.addMarker(
                    MarkerOptions()
                            .position(latlng)
                            .title(getString(R.string.dropped_pin))
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE ))
            )
        }
    }**/



    private fun setPoiClick(map: GoogleMap){
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                    MarkerOptions()
                            .position(poi.latLng)
                            .title(poi.name)
            )
            poiMarker.showInfoWindow()
        }
    }
    private fun setMapStyle(map: GoogleMap){
        try {
            val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this,
                            R.raw.map_style
                    )
            )
            if(!success){
                Log.e(TAG, "Style parsing failed")
            }
        }
        catch (e: Resources.NotFoundException){
            Log.e(TAG, "cant find style, Error: ", e)
        }
    }


    private fun enableMyLocation(map: GoogleMap){

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
        else {
                map.setMyLocationEnabled(true)
                Log.e("tag", "katikati")



                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location == null) {
                        val request = LocationRequest().setPriority(LocationRequest.PRIORITY_LOW_POWER)
                        val callback = object: LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult?) {
                                val location = locationResult?.lastLocation ?:return

                                val latLng = LatLng(location.latitude, location.longitude)
                                val usersLocation = latLng.latitude.toString() + "," + latLng.longitude.toString()
                                val flozzyLocation = homelatlng.latitude.toString()+"," + homelatlng.longitude.toString()
                                map.addMarker(
                                    MarkerOptions().
                                    position(latLng).title("Your Address")
                                )

                                apiResponse(usersLocation, flozzyLocation)


                            }

                        }
                        fusedLocationClient.requestLocationUpdates(request, callback, null)

                    } else {
                        val latLng = LatLng(location.latitude, location.longitude)
                        val usersLocation = latLng.latitude.toString() + "," + latLng.longitude.toString()
                        val flozzyLocation = homelatlng.latitude.toString()+"," + homelatlng.longitude.toString()

                        map.addMarker(
                            MarkerOptions().
                            position(latLng).title("Your Address")
                        )
                        apiResponse(usersLocation, flozzyLocation)

                    }
                }

        }

    }

    private fun apiResponse(usersLocation: String, flozzyLocation: String){
        MapApi.retrofitService.getDistance("AIzaSyDLyORFWLVIETjIiLItGg27xYX2ZeLIx6I", usersLocation, flozzyLocation)
                .enqueue(object : Callback<DirectionResponses>{
                    override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                        drawPolyline(response)
                        getDuration(response)
                        Log.d("bisa dong oke", response.message())
                    }

                    override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                        Log.e("anjir error", t.localizedMessage)
                    }

                })
    }


    private fun drawPolyline(response: Response<DirectionResponses>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        val polyline = PolylineOptions()
                .addAll(PolyUtil.decode(shape))
                .width(8f)
                .color(Color.RED)
        map.addPolyline(polyline)
    }

    private fun getDuration(response: Response<DirectionResponses>){
        val duration = response.body()?.routes?.get(0)?.legs?.get(0)?.distance?.text

    }



    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray) {
        if(requestCode == REQUEST_LOCATION_PERMISSION){
            if(grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                enableMyLocation(map)
            }
        }


    }


}