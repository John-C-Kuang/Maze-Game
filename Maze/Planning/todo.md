
## TODO:

## Completed: 

Note: Our current design already has a relaxed restriction on 7x7 board. There is a single point of control
	to change this to be something else. We also have a single point of control for defining which rows/columns
	are movable. This is why no commits address this revision.

[7.1 - Wrote integration tests. Cleaned up some stuff](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/931159d3579ce581154c569280f2452b4a6efb84)
Created the integration test harness and added some missing documentations.

[7.2 - Fixed referee winner finding](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/09bacd3db55dbb69abebecec5a3633e4c3c6638f)
Takes care of:
- Bug for finding winners on referee

[7.2 - Moved path from gems to drawing, removed Path::toString, refcctor `GameTile::toString`](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/aa1c5a763276fc3f1d856c3dfe40906f24134f8):
Takes care of:
- Move get path from gems to drawing.kt
- Remove toString in Path
- toString in Tiles is slightly expensive

[7.2 - Move slide action check to state from strategy and minor refactors to board
](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/f4331cd26c5fb5b1b0e415f6091892d6096a6938)

Takes care of:
- check slide action should be part of state checking validity, not in strategy
- remove Board::copy, refactor tiles to be kotlin List
- add Hashcode to Board

[7.2 - Created round data so state can keep track of elapsed rounds, referee now actually counts rounds](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/c2da3725319e6f4783d1eec1dfe8815fb4ab7fb8):

Takes care of:
- Referee should check rounds not turns

[7.3 - Enforced homes being unique, refactor getting movable rows/cols into baord, renamed state `winner` field](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/cd460b2a7493a76ab2bb6493b51d0ba47a757823)
[Changes by johncky](Changes by johnkcy) (had to to 2 because of CodeWithMe marking changes weirdly)
Takes care of:
- Rename `winner`/ document field name
- Enforce player home tiles being unique.
- Moved movable rows to Board (was in strategy???)

[7.3 - Wrote test](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/c66952b2ff4858bf2994ba4e576124dba3bc2cf9)
Takes care of writing integration tests

[7.3 - Fixed skipping bugs](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/807e32d01ed61fd6fbd2b64013b5ddba54c0b8b6)
Was to remove a bug we introduced (theorem: touch code -> en-bug)

[7.4 - Wrote tests according to spec](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/50f4eb7b46e6e9650ac40fb5f60090734de3a0a9)
[7.4 - Wrote tests according to spec](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/024aa05633703fb8bed5f4a2c5bd69c052527c08)
[7.4 - Fixed tests](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/e6d3fa5942fa7cf533b76460f3b1a4a7ac350dbd)
Were writing integration tests and fixing them upon closer information of the spec

[7.4 - Completed game over information](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/b38fd722281125186c2db44ad5b1fed9faa16d09)
[Changes by johnkcy](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/1e4d5b5617f5dd6a9347c1a7aa79ce2745d89236)
Takes care of:
- Include players who got kicked out, in what round at the end of game

[7.4 - Design Task](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/06efbb42642b850065becb780d431c30db4b5543)
[7.4 - Design task nit change](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/c11ff7f7d6afac8b84c293860c3339cca507d714)
[7.4 - Finish design task](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/7d3b330366167096b1d0075d0f188168a2bf652c)
[7.4 - Checked design](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/1af58a390dd383f44f62a8068a0760438946ec5a)
Were for the design task

We decided to leave this as is:
- PlayerMechanism and state's players are parallel (decided to keep)

[7.4 - Finished TODO](https://github.khoury.northeastern.edu/CS4500-F22/egoless-armadillos/commit/22fc68d28bf2bd417289ba6799a0a69dcdcf7500)
Was for the TODO



