package com.example.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplaces.adapter.HappyPlaceAdapter
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.models.HappyPlaceModel

private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            fabAddHappyPlace.setOnClickListener {
                startActivityForResult(Intent(this@MainActivity, AddHappyPlaceActivity::class.java), ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }
        getHappyPlacesListFromLocalDB()
    }
    private fun getHappyPlacesListFromLocalDB(){
        binding.apply {
            val dbHandler = DatabaseHandler(this@MainActivity)
            val getHappyPlaceList: ArrayList<HappyPlaceModel> = dbHandler.getHappyPlaceLIst()

            if(getHappyPlaceList.size > 0){
                rvHappyPlacesList.visibility = View.VISIBLE
                tvNoRecordsAvailable.visibility = View.GONE
                setupHappyPlaceRecyclerView(getHappyPlaceList)
            }else{
                rvHappyPlacesList.visibility = View.GONE
                tvNoRecordsAvailable.visibility = View.VISIBLE
            }
        }
    }

    private fun setupHappyPlaceRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>){
        binding.rvHappyPlacesList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            val placesAdapter = HappyPlaceAdapter(this@MainActivity, happyPlaceList)
            adapter = placesAdapter
            placesAdapter.setOnClickListener(object : HappyPlaceAdapter.OnClickListener{
                override fun onClick(position: Int, model: HappyPlaceModel) {
                    val intent = Intent(this@MainActivity, HappyPlaceDetail::class.java)
                    // intent에 Serializable?로 넣을 수 있어서 model을 Serializable로 바꾼다
                    intent.putExtra(EXTRA_PLACE_DETAILS, model)
                    startActivity(intent)
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getHappyPlacesListFromLocalDB()
            }else{
                Log.e("Activity", "Cancelled or Back pressed")
            }
        }
    }

    companion object{
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_PLACE_DETAILS = "extra_place_details"
    }
}