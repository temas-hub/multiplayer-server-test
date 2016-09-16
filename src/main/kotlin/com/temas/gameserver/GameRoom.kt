package com.temas.gameserver

import io.nadron.app.PlayerSession
import io.nadron.app.impl.GameRoomSession

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 30.08.2016
 */
class GameRoom(builder: GameRoomSessionBuilder) : GameRoomSession(builder) {

    override fun onLogin(playerSession: PlayerSession?) {

    }
}