package com.example.happyplaces.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import com.example.happyplaces.models.HappyPlaceModel

class HappyPlaceDetail : AppCompatActivity() {
    private lateinit var binding: ActivityHappyPlaceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var happyPlaceDetailModel: HappyPlaceModel? = null

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            // Serializable을 다시 HappyPlaceModel로 변경
            // Parcelable을 다시 "
            happyPlaceDetailModel =
                    intent.getParcelableExtra(
                            MainActivity.EXTRA_PLACE_DETAILS)
        }

        binding.apply {
            happyPlaceDetailModel?.also {
                setSupportActionBar(toolbarHappyPlaceDetail)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.title = happyPlaceDetailModel.title
                toolbarHappyPlaceDetail.setNavigationOnClickListener {
                    onBackPressed()
                }
            }

            ivPlaceImage!!.setImageURI(Uri.parse(happyPlaceDetailModel!!.image))
            tvDescription!!.text = happyPlaceDetailModel!!.description
            tvLocation!!.text = happyPlaceDetailModel!!.location
            btnViewOnMap.setOnClickListener {
                val intent = Intent(this@HappyPlaceDetail, MapActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, happyPlaceDetailModel)
                startActivity(intent)
            }
        }

    }
}