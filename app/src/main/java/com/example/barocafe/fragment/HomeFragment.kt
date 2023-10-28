package com.example.coffeecart.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.barocafe.R
import com.example.barocafe.adapter.MyDrinkAdapter
import com.example.barocafe.databinding.FragmentHomeBinding
import com.example.barocafe.evenbus.updateCartEvent
import com.example.barocafe.listener.ICartLoadListener
import com.example.barocafe.listener.IDrinkLoadListener
import com.example.barocafe.model.CartModel
import com.example.barocafe.model.DrinkModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeFragment : Fragment(), IDrinkLoadListener, ICartLoadListener {
    private lateinit var binding: FragmentHomeBinding
    lateinit var drinkLoadListener: IDrinkLoadListener
    lateinit var cartLoadListener: ICartLoadListener
    private lateinit var navControl : NavController

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().hasSubscriberForEvent(updateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(updateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public fun onUpdateCartEvent(event: updateCartEvent) {
        countCartFromFireBase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        navControl = Navigation.findNavController(view)
        loadDrinkFromFireBase()
        countCartFromFireBase()
    }

    private fun countCartFromFireBase() {
        val cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Cart").child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(cartSnapshot in snapshot.children){
                            val cartModel = cartSnapshot.getValue(CartModel::class.java)
                            cartModel!!.key = cartSnapshot.key
                            cartModels.add(cartModel)
                        }
                        cartLoadListener.onLoadCartSucess(cartModels)
                        // In ra Logcat để kiểm tra dữ liệu đã tải
                        for (model in cartModels) {
                            Log.d("FirebaseData", "Name: ${model.name}, Image: ${model.image}, Price: ${model.price}")
                        }
                    }else{
                        cartLoadListener.onLoadCartFailed("Drink items not exists!!!")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }
            })
    }

    private fun loadDrinkFromFireBase() {
        val drinkModels : MutableList<DrinkModel> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Drink")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(drinkSnapshot in snapshot.children){
                            val drinkModel = drinkSnapshot.getValue(DrinkModel::class.java)
                            drinkModel!!.key = drinkSnapshot.key
                            drinkModels.add(drinkModel)
                        }
                        drinkLoadListener.onLoadSuccess(drinkModels)

                    }else{
                        drinkLoadListener.onLoadFailed("Drink items not exists!!!")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    drinkLoadListener.onLoadFailed(error.message)
                }
            })

    }

    private fun init(view: View) {
        drinkLoadListener = this
        cartLoadListener = this

        val gridLayout  = GridLayoutManager(requireContext(), 2)
        binding.rvList.layoutManager = gridLayout

        binding.btnCart.setOnClickListener {
            navControl.navigate(R.id.action_homeFragment_to_cartFragment)
        }

        binding.btnSearchView.setOnClickListener {
            navControl.navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    override fun onLoadSuccess(drinkModel: List<DrinkModel>) {
        val adapter = MyDrinkAdapter(requireContext(), drinkModel!!, cartLoadListener)
        binding.rvList.adapter = adapter
    }

    override fun onLoadFailed(message: String?) {
        Toast.makeText(context,"", Toast.LENGTH_SHORT).show()
    }

    override fun onLoadCartSucess(cartModeList: List<CartModel>) {
        var cartSum = 0
        for(cartModel in cartModeList!!){
            cartSum += cartModel!!.quantity
        }
        binding.badge.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Toast.makeText(context,"Empty", Toast.LENGTH_SHORT).show()
    }


}