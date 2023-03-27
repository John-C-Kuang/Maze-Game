# MEMORANDUM

TO: CS4500 Staff

FROM: David Yan, Jose Sulaiman Manzur

DATE: Oct 13, 2022

Subject: Player Interface

Dear CEOs, we are now proposing a design for the data representation and interface that we will provide our customers
so they can implement their game AIs.

### Data Representation for a Player's knowledge
A Player should know: 
- Player state
  - A player must know about itself: It should know its unique identifier, its home tile, its goal treasure, and
whether it has found a treasure.
  - ID: UUID, HomeTile: GameTile, goal: Treasure, foundTreasure: Boolean
- Board and Board State
  - The player must also know the current state of the board: the tiles' connector, treasure locations, players' homes, 
  and the players locations on the board (including itself), player homes, and player names. The player should also know
  whose turn it currently is, what the spare tile is. 
  - boardOfTiles: Array<Array<GameTile>>, spareTile: GameTile, playerNames: Set<Strings>
- Win/lose condition and state
  - A player should know whether they or any other player have won, and if they game has ended/lost. A player should also
  be notified of a referee's punishment for misbehaving or cheating like notification of removal from the game.
  - gameOver: Boolean, winner: String/UUID, penaltyNotification: String

### Player Interface
- Decide whether to make a turn or pass, also determine when
  - activeTurnListener(input: InputStream, output: OutputStream, calculateMove: () -> Unit): Unit //this requires an active connection with the server
  - doPlayerAction(output: OutputStream) // a player should have the board and player state
- Decide how to make a turn
  - determineRowColToSlide(): Row | Column // a player should know the board state
  - determineDirectionToSlide(): HorizontalDirection // a player should know the board state
  - rotateSpareTile(): Degree (of multiple of 90) // a player should know the spare tile
  - calculateMove(output: OutputStream): Coordinates // a player should know the board state
- Get the state of the game:
  - requestBoardState(input: InputStream, output: OutputStream) 
  - requestPlayerQueue(input: InputStream, output: OutputStream)
- Join a game
  - joinMazeGame(serverHost: IP_ADDRESS, serverPort: Int) // player must not be a in a game
- Leave the current game
  - leaveGame(output: outputStream) // player should be in a valid game
- Enter money details
  - submitDeposit(amount: Double, routingNumber: Int, accountNumber:Int, output: OutputStream)
- send ready command
  - readyToStart(output: OutputStream) // player must be in an idle game