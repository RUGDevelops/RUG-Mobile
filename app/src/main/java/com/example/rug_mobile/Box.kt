package com.example.rug_mobile

data class Box(
    val addAccessLog: Boolean,
    val boxId: Int,
    val deliveryId: Int,
    val doorIndex: Int,
    val isMultibox: Boolean,
    val latitude: Int,
    val longitude: Int,
    val qrCodeInfo: String,
    val terminalSeed: Int,
    val tokenFormat: Int
)