package eu.virtusdevelops.rug_mobile

data class Box(
    val deliveryId: Int,
    val boxId: Int,
    val tokenFormat: Int,
    val latitude: Int,
    val longitude: Int,
    val qrCodeInfo: String,
    val terminalSeed: Int,
    val isMultibox: Boolean,
    val doorIndex: Int,
    val addAccessLog: Boolean,
    )