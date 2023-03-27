Memorandum  
**TO**: CS4500 Course Staff (CEOs of our soon-to-be unicorn)  
**FROM**: David Yan, Jose Sulaiman Manzur  
**DATE**: Sep 30, 2022  
**SUBJECT**: Three Sprint Planning  
After deliberating with the (two-man) team, we have come up with the following plan for our first three sprints. We estimate each
sprint will take about 16 hours of work. We will begin by designing the first game you want to implement: Maze. We know we
aim at having support for multiple games, but we want to design a concrete game first so we can develop a better "abstract"
design for games and then implement the server.
### Sprint 1 (ends 10/8)
- Design document for the game components have been written. (2 hours)
- Design document for (player/referee) protocol has been written. (2 hours)
- Data representations of Maze components (board, tiles, treasure,gems) are implemented. (3 hours)
  - The game components we wish to design this spring are the game board and tiles, the gems, the players, and the referee.
- Game logic implementation is in progress. (9 hours)
  - Unit tests exist for game logic.
  - Sliding
  - Rotating tile
  - Placing tile
  - Assigning home and treasure location

### Sprint 2 (ends 10/15)

This sprint will focus on completing the game logic implementation, which began last print.
We will also continue the development of a static UI. After these components have been
created, we will have a better idea of how to design the server.

- Game logic implementation is finished. (10 hours)
  - Moving players
  - Capturing treasure
  - Game termination
- Simple static UI of the board is developed (3 hours)
- Design document for server has begun (3 hours)

### Sprint 3 (ends 10/22)

Once the game implementation is finished, we can create a controller to actually play the game.
Finishing this will open up development for the server.

- Design document for server finished. (3 hours)
- First iteration of MVC interfaces are completed (2 hours)
- Implement a single-player controller. (2 hours)
- Implement a responsive view integrated with the controller. (3 hours)
- Begin implementing the server. (6 hours)