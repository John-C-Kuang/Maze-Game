package serialization.data

data class StateDTO(
    val board: BoardDTO,
    val spare: TileDTO,
    val plmt: List<PlayerDTO>,
    val last: List<String>?
)