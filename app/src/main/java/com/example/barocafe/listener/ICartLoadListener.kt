package com.example.barocafe.listener

import com.example.barocafe.model.CartModel

interface ICartLoadListener {
    fun onLoadCartSucess(cartModeList: List<CartModel>)
    fun onLoadCartFailed(message:String?)
}