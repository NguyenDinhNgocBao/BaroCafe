package com.example.barocafe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.barocafe.R
import com.example.barocafe.evenbus.updateCartEvent
import com.example.barocafe.model.CartModel
import com.google.firebase.database.FirebaseDatabase
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class MyCartAdapter(private val context: Context,private val list: List<CartModel>):RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {
    class MyCartViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var imageView: ImageView? = null
        var txtName: TextView? = null
        var txtPrice: TextView? = null
        var btnPlus:ImageView? = null
        var btnMinus:ImageView? = null
        var txtquantity:TextView? = null
        var btnDelete = itemView.findViewById(R.id.btnDelete) as ImageView
        init {
            imageView = itemView.findViewById(R.id.imgLogo) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
            btnMinus = itemView.findViewById(R.id.btnMinus) as ImageView
            btnPlus = itemView.findViewById(R.id.btnPlus) as ImageView
            txtquantity = itemView.findViewById(R.id.txtQuantity) as TextView
            btnDelete = itemView.findViewById(R.id.btnDelete) as ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        return MyCartViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].image) // Đường dẫn URL của hình ảnh
            .into(holder.imageView!!)

        holder.txtName!!.text = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text = StringBuilder("$").append(list[position].price).append("d")
        holder.txtquantity!!.text = StringBuilder().append(list[position].quantity)

        //Event
        holder.btnMinus!!.setOnClickListener {_ -> minusCartItem(holder,list[position]) }
        holder.btnPlus!!.setOnClickListener {_ -> plusCartItem(holder,list[position]) }
        holder.btnDelete!!.setOnClickListener {_ ->
            val dialog = AlertDialog.Builder(context).setTitle("Delete product ?")
                .setMessage("Do you wanna delete this item ?")
                .setNegativeButton("Cancel"){dialog,_ -> dialog.dismiss()}
                .setPositiveButton("Ok"){dialog,_ ->
                    notifyItemRemoved(position)
                    FirebaseDatabase.getInstance().getReference("Cart").child("UNIQUE_USER_ID")
                        .child(list[position].key!!)
                        .removeValue()
                        .addOnSuccessListener {
                            EventBus.getDefault().postSticky(updateCartEvent())
                        }
                }
                .create()
            dialog.show()
        }
    }

    private fun plusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        cartModel.quantity += 1
        cartModel.totalPrice = cartModel!!.quantity * cartModel.price!!.toFloat()
        holder.txtquantity!!.text = StringBuilder().append(cartModel.quantity)
        updateFirebase(cartModel)
    }

    private fun minusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        if(cartModel.quantity > 1){
            cartModel.quantity -= 1
            cartModel.totalPrice = cartModel!!.quantity * cartModel.price!!.toFloat()
            holder.txtquantity!!.text = StringBuilder().append(cartModel.quantity)
            updateFirebase(cartModel)
        }else if(cartModel.quantity == 1){
            val dialog = AlertDialog.Builder(context).setTitle("Delete Item ?")
                .setMessage("?")
                .setNegativeButton("OK") {dialog, _ -> dialog.dismiss()}
                .create()
            dialog.show()
        }
    }

    private fun updateFirebase(cartModel: CartModel) {
        FirebaseDatabase.getInstance().getReference("Cart")
            .child("UNIQUE_USER_ID")
            .child(cartModel.key!!)
            .setValue(cartModel)
            .addOnSuccessListener {
                EventBus.getDefault().postSticky(updateCartEvent())
            }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}