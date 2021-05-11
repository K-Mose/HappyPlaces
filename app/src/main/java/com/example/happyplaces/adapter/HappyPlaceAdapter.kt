package com.example.happyplaces.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.activities.AddHappyPlaceActivity
import com.example.happyplaces.activities.MainActivity
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.PlaceRowBinding
import com.example.happyplaces.models.HappyPlaceModel

class HappyPlaceAdapter(
        val context: Context,
        val list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<HappyPlaceAdapter.MyViewHolder>(){

    private var onClickListener: OnClickListener? = null

    private lateinit var binding: PlaceRowBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // 기존
        // LayoutInflater.from(context).inflate(R.layout.place_row, parent, false))
        binding = PlaceRowBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            holder.binding.apply {
                ivRowPlaceImage.setImageURI(Uri.parse(model.image))
                tvTitle.text = model.title
                tvDescription.text = model.description
            }
            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    // bounded function with interface
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int){
        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    fun removeAt(position: Int){
        val dbHandler = DatabaseHandler(context)
        val isDelete = dbHandler.deleteHappyPlace(list[position])
        if(isDelete > 0){
            list.removeAt(position)
            // 아이템이 제거되었다고 알림
            notifyItemRemoved(position)
        }
    }

    // interface
    // 자세한 개념은 188강 11:54
    interface OnClickListener{
        fun onClick(position: Int, model: HappyPlaceModel)
    }

    class MyViewHolder(val binding: PlaceRowBinding) : RecyclerView.ViewHolder(binding.root)
}