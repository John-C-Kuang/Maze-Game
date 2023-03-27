Memorandum  
**TO**: CS4500 Course Staff (CEOs of our soon-to-be unicorn)  
**FROM**: David Yan, Jose Sulaiman Manzur  
**DATE**: November 11th, 2022  
**SUBJECT**: Distributed System Wishlist

CS4500 Course instructors,  
We've realized to make this product profitable we need clients off of our server. Here is our proposal to have remote
clients connect to our game server and how the referee will interact with them and manipulate the game state on their 
behalf.

# Remote Player Proxy
Players connecting to our game will be done through the remote proxy pattern where a RemotePlayerMechanism (implements PlayerMechanism)
wraps the TCP connection (input/output streams) to our proxy server. Our already implemented referee will be able to use these
mechanisms without further expansion, as the referee is abstracted over the exact communication pipeline the players use. 
Currently, our referee is handed PlayerMechanisms that has direct calls in the same runtime on server-side pseudo clients
but in the future  our referee will be handed PlayerMechanisms that relay commands like setupAndUpdateGoal, proposeBoard,
takeTurn etc. through TCP in a defined referee-player protocol.

## Board set up
Our proxy server will start by listening for TCP connections. The proxy server will then create a RemotePlayerMechanism instance on
the player's behalf, using the given name and assigning a random home and goal tile.
Once the desired number of players, (set in the game setting), have signed up, the server will then create a referee instance 
instantiating it with the RemotePlayerMechanisms.

The referee will then query all players for a board suggestion (removing any exception-raising, non-terminating devils),
and use whatever criteria it wants to select the starting board.

### Function Calls and Data Definitions
`name`
	- server request: { "request": "name" }
    - client response: <String>
`proposeBoard`
	- server request: { "request": "proposeBoard", "width": <Int>, "height": <Int> }
	- client response: {connectors: Matrix<Connector>, Matrix<Treasure>}
		Where a Matrix is a 2D list, Connector is one of the acceptable characters,
		and treasure is a pair of valid gem names.
`setup`
	- server request: { "request": "setup", "goal_coords": [<Int>, <Int>], "state": State | null }
		Where a State is an object with a board, a spare tile, a list of player's public data (current position, home tile, color),
		and an action. As it was defined in the previously defined specification. ie. { "board": Board, "spare_tile": Tile, 
"players": [{"current_pos": [<Int><Int>, "home_tile": Tile, "color": <String>, "last_action": [<Int>, "UP" | "DOWN" | "LEFT" | "RIGHT", <Int>, [<Int>, <Int>]] | "Pass"]]

## Starting the Game and Playing the Game
Once the board is chosen, the referee will communicate the board status and the player's goal individually and in order (increasing age).
The referee starts the game by requesting the clients in order of age for a turn. Sending each player the current state and 
taking interpreting its move, acting on the state on its behalf.
If any player misbehaves or cheat, it will be kicked from the game.

If a player reaches its treasure goal, it will send a `setUp` message with a null state, and a reminder of where 
home is in the goal field.

![Sequence diagram of 4 users and a game server.](proxy_server_cs4500.PNG)

### Function Calls and Data Definitions
`takeTurn`
	- server request: {"request": "takeTurn", "currentState": State}
	- client response: {"move": [<Int>, "UP" | "DOWN" | "LEFT" | "RIGHT", <Int>, [<Int>, <Int>]] | "Pass"}
`setUp`
	- server request: {"request": "takeTurn", "currentState":null, "goal": Coordinate}


## Ending the game
The referee will notify which players have won and which players lost. The referee will make a call to each 
player mechanism about their win state and the remote player mechanism will send the information downstream.

### Function Calls and Data Definitions
`won`
	- server request: {"request": "won", "won": Boolean}