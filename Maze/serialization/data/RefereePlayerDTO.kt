package serialization.data

data class RefereePlayerDTO(
    val current: CoordinateDTO,
    val home: CoordinateDTO,
    val goto: CoordinateDTO,
    val color: String
)