package serialization.data

data class BoardDTO(
   val connectors: List<List<String>>,
   val treasures: List<List<List<String>>>
)