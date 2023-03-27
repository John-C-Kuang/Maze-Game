package Players

import Common.player.PlayerData


/**
 * The ordering strategy that weighs alternative tiles based on their row value and then column value (top left is
 * first, bottom right is last).
 */
class Riemann(player: PlayerData): AbstractOrderingStrategy(
    compareBy({ coord -> coord.row.value  }, { coord -> coord.col.value }), player)
