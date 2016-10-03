package com.temas.gameserver.aimmaster

import io.nadron.app.Session
import io.nadron.event.Event
import io.nadron.event.impl.DefaultSessionEventHandler

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 21.09.2016
 */


class SessionEventHandler(session : Session, val room: GameRoom) : DefaultSessionEventHandler(session) {

    override fun onDataIn(event: Event) {
    }



}