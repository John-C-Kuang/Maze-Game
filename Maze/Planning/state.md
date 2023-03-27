Memorandum  
**TO**: CS4500 Course Staff (CEOs of our soon-to-be unicorn)  
**FROM**: David Yan, Jose Sulaiman Manzur  
**DATE**: October 7th, 2022  
**SUBJECT**: State Wishlist

CS4500 Course instructors,  
After thorough analysis, the engineering team has determined we
need a moderator controller component, aka. a referee. Below I've provided a set of goals that our
engineers will undergo to implement this. A delivery of this component will make a lot of money... I mean customer satisfaction!
### Referee state (game state)
- State of the game
  - nextAction: Enum { SLIDE, ROTATE, MOVE } // the current state of the game, whether what move is expected for the board
  - currentPlayerID: String // a unique id assigned to each player,
- players: Set<Player<{id: String, goal: Tile, home: Tile, location: Coordinates, treasureFound: Boolean } // referee keeps track
  players and their attributes
- board: Board // board to play on and manipulate
- randomSeed: Int // used to set random seed for board generation
### Referee functions
- handle and respond to player actions
  - checkMobilityFromCoordinates(row: Int, col: Int): Boolean
  - getPlayerOptions(playerId: String): Response
  - executePlayerAction(action: Enum { PASS, ACTION }, playerId: String): Response*
  - movePlayer(row: Int, col: Int, playerId: String): Response
  - slide(rowOrColPosition: Int, playerId: String): Response
  - rotateSpareTile(degrees: Int, playerId: String): Response
- add new players to the game
  - addNewPlayer(): Response
  - generateID(): Response
- remove player from the game
  - removePlayer() Response

Response is an object of guaranteed shape { status: Enum { SUCCESS, FAIL }, message: str, board: Board }. 
This shape is open to extension, and will be specific to each method.
