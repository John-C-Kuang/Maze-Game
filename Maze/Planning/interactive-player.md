# MEMORANDUM

TO: CS4500 Staff

FROM: David Yan, Jose Sulaiman Manzur

DATE: Oct 27, 2022

Subject: Interactive player mechanism

Dear CEOs,  
We are now ready to expand to customers who do not wish to use the command line 
interface/custom programs to play Maze. Here is our proposal for a client program with a graphical user 
interface:

## Maze Client
This client shall be split into two sections: View, Controller.

### Controller
The controller is the entry point of the program that starts communication with the server through maintaining
a TCP connection
 and starting the graphical user interface (GUI). The controller updates the view whenever
the server sends new information. The controller will implement certain functions of an interface that the
view can call. The view can call methods when the user is ready to communicate with the server namely,
submitting a move/pass to the server or leaving the game.


### View
The view can be a graphical user interface. The board should be displayed with tiles, paths, rotated by its degrees,
player homes, treasures, and player positions.
Additionally, the spare tile should be displayed. Buttons to slide rows/cols and rotate the spare tile should
be presented; these buttons are clickable when it is a player's turn along with a button to submit a move 
when a valid move has been inputted. Additionally, a player can submit their name on startup. 