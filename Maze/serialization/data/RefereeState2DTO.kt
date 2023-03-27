package serialization.data

data class RefereeState2DTO(
    val board: BoardDTO,
    val spare: TileDTO,
    val goals: List<CoordinateDTO>?,
    var plmt: List<RefereePlayerDTO>,
    val last: List<String>?
)


