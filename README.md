CS4500

For all assignments about Software Development.

Three components for the project: 
1. Referee and state: The rule-enforcer and game data. 
2. PlayerMechanism: Represents the basic functionalities a player supports (setup, take-turn, win)   
3. Server and Client: Allows remote interactions between remote players and the game server 


* [Maze/Client](./Maze/Client) contains implementation of the client
* [Maze/Common](./Maze/Common) contains implementation for game state and the game board along with the components needed
* [Maze/Players](./Maze/Players) Player mechanism, Strategy(includes Euclid and Riemann)
* [Maze/Referee](./Maze/Referee) Referee of the game along with its component(EndGameData) & Observer
* [Maze/Remote](./Maze/Remote) Server-side components including player-proxy, referee-proxy, methodCall(transfer game internal data representation to json)
* [Maze/Server](./Maze/Server) contains implementation of the game server
* [Maze/serialization](./Maze/serialization) contains data representation and connverters to converts these DTO to data
   
* [B/](./B) contains TAHBPL-B script (xcollects) and logic
  * [Other/](./B/Other) contains the program logic in B.kt
* [C/](./C) contains TAHBPL-C script, tests, and logic
  * [Other/](./C/Other) Data.kt contains data definitions while XJson contains logic to parse JSON
  * [Tests/](./C/Tests) test pairs with inputs and expected output
* [D/](./D) contains TAHBPL-D script and logic
  * [Other/](./D/Other)  contains tests, data definitions, and GUI logic 
    * [Tests/](./D/Other/Tests) contains inputs to initialize gui tests
* [E/](./E) contains TAHBPL-E script and logic 
  * [Other/](./E/Other) TCP program logic
    * [C/](./E/Other/C) copied from C assignment, 
  * [Tests/](./E/Tests) contains input output test pairs
* [gradle/](./gradle) contains gradle jar and configs
* [build.gradle.kts](./build.gradle.kts) contains the dependencies, runtime, and jar configurations for the gradle build system
* [README.md](./README.md)
