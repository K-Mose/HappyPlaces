package com.example.happyplaces.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.databinding.PlaceRowBinding
import com.example.happyplaces.models.HappyPlaceModel

class HappyPlaceAdapter(val context: Context, val items: ArrayList<HappyPlaceModel>) :
    RecyclerView.Adapter<HappyPlaceAdapter.ViewHolder>(){

    private lateinit var binding: PlaceRowBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = PlaceRowBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            ivRowPlaceImage.setImageURI(Uri.parse(items[position].image))
            tvTitle.text = items[position].title
            tvDescription.text = items[position].description
        }
    }

    class ViewHolder(val binding: PlaceRowBinding) : RecyclerView.ViewHolder(binding.root){

    }
}