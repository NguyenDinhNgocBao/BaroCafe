package com.example.barocafe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.barocafe.R
import com.example.barocafe.evenbus.updateCartEvent
import com.example.barocafe.listener.ICartLoadListener
import com.example.barocafe.listener.IRecyclerClickListener
import com.example.barocafe.model.CartModel
import com.example.barocafe.model.DrinkModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class MyDrinkAdapter(private val context: Context, private val list: List<DrinkModel>, private val cartListener:ICartLoadListener): RecyclerView.Adapter<MyDrinkAdapter.MyDrinkViewHolder>(){

    class MyDrinkViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var imageView: ImageView? = null
        var txtName:TextView? = null
        var txtPrice:TextView? = null
        var btnAdd: ImageButton? = null
        private var clickListener:IRecyclerClickListener? = null

        fun setClickListener(clickListener: IRecyclerClickListener){
            this.clickListener = clickListener
        }

        init {
            imageView = itemView.findViewById(R.id.imgLogo) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
            btnAdd = itemView.findViewById(R.id.btnAddToCart) as ImageButton
            //Add
            //itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            clickListener!!.onItemClickListener(p0, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDrinkViewHolder {
        return MyDrinkViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_drink_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyDrinkViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].image) // Đường dẫn URL của hình ảnh
            .into(holder.imageView!!)

        holder.txtName!!.text = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text = StringBuilder().append(list[position].price).append("d")

        //Add to Cart
        /*holder.setClickListener(object : IRecyclerClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
                addToCart(list[position])
            }
        })
        */
        holder.btnAdd!!.setOnClickListener{
            addToCart(list[position])
        }
    }

    private fun addToCart(drinkModel: DrinkModel) {
        val userCart = FirebaseDatabase.getInstance().getReference("Cart").child("UNIQUE_USER_ID")
        userCart.child(drinkModel.key!!).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    //If item has in Cart, just Update
                    val cartModel =snapshot.getValue(CartModel::class.java)
                    val updateCart:MutableMap<String, Any> = HashMap()
                    cartModel!!.quantity = cartModel!!.quantity+1
                    updateCart["quantity"] =cartModel!!.quantity
                    updateCart["totalPrice"] = cartModel!!.quantity * cartModel.price!!.toFloat()

                    userCart.child(drinkModel.key!!).updateChildren(updateCart)
                        .addOnSuccessListener {
                            EventBus.getDefault().postSticky(updateCartEvent())
                            cartListener.onLoadCartFailed("Success Add To Cart")
                        }
                        .addOnFailureListener {
                            err ->cartListener.onLoadCartFailed(err.message)
                        }
                }else{
                    //if item not current in Cart, add to cart
                    val cartModel = CartModel()
                    cartModel.key = drinkModel.key
                    cartModel.name = drinkModel.name
                    cartModel.image = drinkModel.image
                    cartModel.price = drinkModel.price
                    cartModel.quantity = 1
                    cartModel.totalPrice = drinkModel.price!!.toFloat()

                    userCart.child(drinkModel.key!!).setValue(cartModel)
                        .addOnSuccessListener {
                            EventBus.getDefault().postSticky(updateCartEvent())
                            cartListener.onLoadCartFailed("Success Add To Cart")
                        }
                        .addOnFailureListener {
                                err ->cartListener.onLoadCartFailed(err.message)
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                cartListener.onLoadCartFailed(error.message)
            }

        })
    }

    override fun getItemCount(): Int {
        return list.size
    }
}