package com.temas.gameserver.aimmaster

import io.nadron.app.PlayerSession
import io.nadron.app.impl.GameRoomSession
import io.nadron.event.SessionEventHandler
import org.slf4j.LoggerFactory

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 30.08.2016
 */
class GameRoom(builder: GameRoomSessionBuilder) : GameRoomSession(builder) {

    private val LOG = LoggerFactory.getLogger(GameRoom::class.java)

    override fun onLogin(playerSession: PlayerSession) {
        LOG.debug("Player with name: '${playerSession.player.name}' was logged in")
        playerSession.addHandler(SessionEventHandler(playerSession, this))
    }
}