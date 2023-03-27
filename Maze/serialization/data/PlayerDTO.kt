package serialization.data

data class PlayerDTO(
    val current: CoordinateDTO,
    val home: CoordinateDTO,
    val color: String
)