## Components
- Referee
	- timeout duration for player input
	- kicking people out for misbehaving/cheating
	- board set up
- Protocol/Player interface
	- requesting money
	- signing players

- Player implementation
	- implements the protocol
		- playing
		- passing turns

- Server
	- maintain connection
	- update observers
	- able to host multiple games on multiple ports
- Client
	- display GUI and gamestate
	- error handle joining non-existing
- Business logic
	- Board
	- tiles
	- treasure
	- avatar
	- castle
	- Rotating tile
	- Shifting rows and columns
	- Home and treasure locations
	- movement
	- insert spare
## Functional Requirements
- Removing players that cheats or otherwise misbehaves
## Non Functional Requirements
- Error handling