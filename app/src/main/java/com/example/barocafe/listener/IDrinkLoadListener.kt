package com.example.barocafe.listener

import com.example.barocafe.model.DrinkModel


interface IDrinkLoadListener {
    fun onLoadSuccess(drinkModel: List<DrinkModel>)
    fun onLoadFailed(message:String?)
}