package com.example.smoothieshopapp.model

data class Smoothie(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val price: Float = 0.0f,
    val rating: Float = 0.0f,
    val quantityInStock: Int = 0
) {
    constructor(id: String, mapSmoothie: Map<String, Any?>) : this(
        id,
        mapSmoothie["name"].toString(),
        mapSmoothie["imageUrl"].toString(),
        mapSmoothie["price"].toString().toFloatOrNull() ?: 0.0f,
        mapSmoothie["rating"].toString().toFloatOrNull() ?: 0.0f,
        mapSmoothie["quantityInStock"].toString().toIntOrNull() ?: 0
    )
}

/**
 * This function is used to format price
 */
fun Smoothie.priceFormatted(): String {
    return String.format("%.2f$", this.price)
}

/**
 * This function is used to format rating
 */
fun Smoothie.ratingFormatted(): String {
    return String.format("%.0f", this.rating)
}