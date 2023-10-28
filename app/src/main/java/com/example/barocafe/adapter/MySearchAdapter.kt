package com.example.barocafe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.barocafe.R
import com.example.barocafe.data.DrinkData
import com.example.barocafe.model.DrinkModel
import java.lang.StringBuilder

class MySearchAdapter(private var list: List<DrinkData>): RecyclerView.Adapter<MySearchAdapter.MySearchVewHolder>() {
    class MySearchVewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageView: ImageView? = null
        var txtName: TextView? = null
        var txtPrice:TextView? = null
        init {
            imageView = itemView.findViewById(R.id.imageLogo2) as ImageView
            txtName = itemView.findViewById(R.id.txtTitle2) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice2) as TextView
        }
    }

    fun setFilteredList(list: List<DrinkData>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MySearchAdapter.MySearchVewHolder {
        return MySearchAdapter.MySearchVewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_each_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MySearchAdapter.MySearchVewHolder, position: Int) {
        holder.imageView!!.setImageResource(list[position].logo)
        holder.txtName!!.text = StringBuilder().append(list[position].title)
        holder.txtPrice!!.text = StringBuilder().append(list[position].price)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}