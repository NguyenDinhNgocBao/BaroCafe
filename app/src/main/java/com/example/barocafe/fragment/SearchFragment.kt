package com.example.barocafe.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.barocafe.R
import com.example.barocafe.adapter.MySearchAdapter
import com.example.barocafe.data.DrinkData
import com.example.barocafe.databinding.FragmentSearchBinding
import com.example.barocafe.listener.IDrinkLoadListener
import com.example.barocafe.model.DrinkModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class SearchFragment : Fragment(){
    private lateinit var binding:FragmentSearchBinding
    private var list = ArrayList<DrinkData>()
    private lateinit var adapter: MySearchAdapter
    private lateinit var navControl : NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navControl = Navigation.findNavController(view)

        //Recycle
        binding.rvSearchList.setHasFixedSize(true)
        binding.rvSearchList.layoutManager = LinearLayoutManager(context)
        addDataToList()
        adapter = MySearchAdapter(list)
        binding.rvSearchList.adapter = adapter


        //SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                fliterList(newText)
                return true
            }
        })
    }

    private fun fliterList(newText: String?) {
        if(newText != null){
            val filteredList = ArrayList<DrinkData>()
            for(i in list){
                if(i.title.toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))){
                    filteredList.add(i)
                }
            }
            if(newText.isEmpty()){
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show()
            }else{
                adapter.setFilteredList(filteredList)
            }
        }
    }

    private fun addDataToList() {
        list.add(DrinkData("Tea Lychee", R.drawable.trathachvai, "45.00"))
        list.add(DrinkData("Tea Peach", R.drawable.trathanhdao, "40.00"))
        list.add(DrinkData("Tea Lotus", R.drawable.trasenvang, "40.00"))
        list.add(DrinkData("GreenTea Red Beans", R.drawable.traxanhdaudo, "45.00"))
        list.add(DrinkData("Ice Bac Xiu", R.drawable.bacxiuda, "29.00"))
        list.add(DrinkData("Coffe Black", R.drawable.cfd, "29.00"))
        list.add(DrinkData("Coffe Milk", R.drawable.phinsuada, "29.00"))
        list.add(DrinkData("PhinDi CreamMilk", R.drawable.phindikemsua, "45.00"))
        list.add(DrinkData("Macchiato", R.drawable.macchiato, "60.00"))
        list.add(DrinkData("Cappuccino", R.drawable.cappucino, "60.00"))
        list.add(DrinkData("Americano", R.drawable.americanonong, "60.00"))
    }



}