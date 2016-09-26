package com.temas.gameserver.aimmaster

import io.nadron.app.Game
import io.nadron.app.GameRoom
import io.nadron.app.impl.GameRoomSession
import io.nadron.app.impl.SimpleGame
import io.nadron.protocols.Protocol
import io.nadron.service.LookupService
import io.nadron.service.impl.SimpleLookupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import java.util.*

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 30.08.2016
 */

@Configuration
@ImportResource("classpath:beans.xml")
open class SpringConfig {
    @Autowired
    @Qualifier("messageBufferProtocol")
    private val messageBufferProtocol: Protocol? = null

    fun game() : Game {
        return SimpleGame(1, "aimmaster")
    }

    @Bean(name = arrayOf("gameRoom"))
    open fun gameRoom() : GameRoom {
        val sessionBuilder = GameRoomSession.GameRoomSessionBuilder()
        sessionBuilder.parentGame(game()).gameRoomName("defaultRoom").protocol(messageBufferProtocol)
        return GameRoom(sessionBuilder)
    }


    @Bean(name = arrayOf("lookupService"))
    open fun lookupService(): LookupService {
        val refKeyGameRoomMap = HashMap<String, GameRoom>()
        val gameRoom = gameRoom()
        refKeyGameRoomMap.put(gameRoom.gameRoomName, gameRoom)
        val service = SimpleLookupService(refKeyGameRoomMap)
        return service
    }

}