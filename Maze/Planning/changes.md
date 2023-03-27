Memorandum To: Santa Claus   
From: Chengyi Kuang, Jose Sulaiman   
Date: November 10, 2022   
Subject: Potential Redesigns   

Here are our thoughts for the proposed redesigns. We have ranked them 1-5 (5 being VERY difficult)

Blank tiles for the board: 1  
Justification: Our GameTile object takes in _Path_ enum that represents the open paths on a GameTile. 
We could simply add a new field in _Path_ that represents no available paths (this would be done by having
no directions from which the tile could be reachable). 
This way, the _getReachableTiles_ method should naturally ignore the empty tile.  

Use movable tiles as goals: 3  
Justification: We first need to modify the _slideRowAndInsertSpare_ and _slideColumnAndInsertSpare_ 
methods so that we update the home and treasures tiles for each player on the affected row/column. 
Then, we need to update the strategies as our strategies current assume that the goal is unmovable.  

Ask player to sequentially pursue several goals, one at a time: 2  
Justification: We would only need to modify the referee so that the referee will call _setup_ 
on the player APIs with multiple goals. We may also need to update the _PlayerData_ to store additional fields 
if the player should know about the additional goals.  
The game over conditions would also change.
The currently implemented strategies might become obsolete, although they could be reworked so they
stupidly pursue the first goal, then the second, etc. without knowing about optimization.


