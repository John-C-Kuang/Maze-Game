package serialization.data


data class RefereeStateDTO(
    val board: BoardDTO,
    val spare: TileDTO,
    var plmt: List<RefereePlayerDTO>,
    val last: List<String>?
)