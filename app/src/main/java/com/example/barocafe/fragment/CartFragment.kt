package com.example.barocafe.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barocafe.R
import com.example.barocafe.adapter.MyCartAdapter
import com.example.barocafe.databinding.FragmentCartBinding
import com.example.barocafe.evenbus.updateCartEvent
import com.example.barocafe.listener.ICartLoadListener
import com.example.barocafe.model.CartModel
import com.example.barocafe.model.DrinkModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder
import java.util.zip.Inflater

class CartFragment : Fragment(), ICartLoadListener {
    private lateinit var  binding:FragmentCartBinding
    var ICartLoadListener:ICartLoadListener?= null
    private lateinit var navControl : NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        navControl = Navigation.findNavController(view)
        loadCartFromFirebase()
    }

    private fun loadCartFromFirebase() {
        val cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Cart").child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                        for (cartSnapshot in snapshot.children) {
                            val cartModel = cartSnapshot.getValue(CartModel::class.java)
                            cartModel!!.key = cartSnapshot.key
                            cartModels.add(cartModel)
                        }
                        ICartLoadListener!!.onLoadCartSucess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    ICartLoadListener!!.onLoadCartFailed(error.message)
                }
            })
    }

    private fun init(view: View) {
        ICartLoadListener = this
        val layoutManager = LinearLayoutManager(context)
        binding.rvCart!!.layoutManager = layoutManager
        binding.rvCart!!.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
        binding.btnBack!!.setOnClickListener {
            navControl.navigate(R.id.action_cartFragment_to_homeFragment)
        }
    }

    override fun onLoadCartSucess(cartModeList: List<CartModel>) {
        var sum = 0.0
        for(s in cartModeList!!){
            sum += s!!.totalPrice
        }
        binding.txtTotalPrice.text =  StringBuilder("${sum}").append("d")
        val adapter = MyCartAdapter(requireContext(), cartModeList!!)
        binding.rvCart!!.adapter = adapter
    }

    override fun onLoadCartFailed(message: String?) {
        Toast.makeText(context,"Empty", Toast.LENGTH_SHORT).show()
    }
}