package javafx

import Client.javafx.RefereeObserverApplication
import Referee.EndgameData
import Referee.ObserverMechanism
import Referee.RandomStateReferee
import Server.Server

class RandomStateServerApplication: RefereeObserverApplication() {
    override fun startGame(observer: ObserverMechanism): EndgameData {
        val randomStateReferee = RandomStateReferee()
        randomStateReferee.registerObserver(observer)
        return Server(randomStateReferee).start()
    }
}