package serialization.data

import com.google.gson.annotations.SerializedName

data class TileDTO(
    val tilekey: String,

    @SerializedName("1-image")
    val image1: String,

    @SerializedName("2-image")
    val image2: String
)