package testing

import Common.board.Board
import Common.board.Coordinates
import Common.tile.GameTile
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.InputStreamReader

object TestUtils {



    fun getTilesInRow(rowIndex: Int, board: Board): Array<GameTile> {
        return (0 .. 6)
            .map { board.getTile(Coordinates.fromRowAndValue(rowIndex, it)) }.toTypedArray()
    }

    fun getTilesInCol(colIndex: Int, board: Board): Array<GameTile> {
        return (0 .. 6)
            .map { board.getTile(Coordinates.fromRowAndValue(it, colIndex)) }.toTypedArray()
    }

}
