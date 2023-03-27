# Board

## PlayableBoard Interface

### Methods

 - `slide(row || col, Direction) -> Unit`: Slides the specified row or column into the 
direction.
 - `insertSpareTile() -> Unit`: Inserts the spare tile into the empty space. 
Should be called in a state after a `slide` call.
 - `rotateSpareTile(Degree) -> Unit`: rotates the spare tile by the specified amount.
 - `getReachableTiles(x,y) -> Set<Tile>`: gets the tiles reachable from `(x,y)`
 - `makeRandom(size) -> Unit`: creates a random board.

### Fields

- `board: List<List<Tile>>` represents the rows and columns
- `spareTile: Tile` represents the spare Tile
- `dislogdedTile: Tile?` only exists after a slide, before an insert
- `emptySlot: Pair<XCoordinate, YCoordinate>?` only exists after a slide, before an insert
- `gameState: Enum<idle, pendingInsertion>` represents when a slide or insertion is expected
(invariant: emptySlot being null depends on gameState)
