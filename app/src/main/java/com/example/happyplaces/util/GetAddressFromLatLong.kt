package com.example.happyplaces.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.util.Log
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

// 비동기식 방법이 Deprecated 되어서 이제 coroutines 사용해야 함
class GetAddressFromLatLong
    (context: Context, private val latitude: Double, private val longitude: Double)
    : AsyncTask<Void, String, String>() {
    private val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
    private lateinit var mAddressListener: AddressListener
    override fun doInBackground(vararg params: Void?): String {
        try {
            val addressList: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if(addressList != null && addressList.isNotEmpty()){
                val address: Address = addressList[0]
                // address.toString() :: Address[addressLines=[0:"381-15 Seongnae-dong, Gangdong-gu, Seoul, South Korea"],feature=381-15,admin=Seoul,sub-admin=null,locality=null,thoroughfare=Seongnae-dong,postalCode=134-033,countryCode=KR,countryName=South Korea,hasLatitude=true,latitude=37.5329116,hasLongitude=true,longitude=127.13467,phone=null,url=null,extras=null]
                val sb = StringBuilder()
                for(i in 0..address.maxAddressLineIndex){
                    sb.append(address.getAddressLine(i)).append(" ")
                }
                sb.deleteCharAt(sb.length-1)
                return sb.toString()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return ""
    }

    override fun onPostExecute(resultString: String?) {
        if(resultString == null){
            mAddressListener.onError()
        }else{
            mAddressListener.onAddressFound(resultString)
        }
        super.onPostExecute(resultString)
    }

    fun setAddressListener(addressListener: AddressListener){
        mAddressListener = addressListener
    }

    fun getAddress(){
        execute()
    }

    interface AddressListener{
        fun onAddressFound(address: String?)
        fun onError()
    }
}