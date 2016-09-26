package com.temas.gameserver.aimmaster

import io.nadron.server.ServerManager
import org.apache.log4j.PropertyConfigurator
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 21.09.2016
 */

fun main(args: Array<String>) {
    GameServer().start()
}

class GameServer {
    private val LOG = LoggerFactory.getLogger(GameServer::class.java)

    fun start() {
        //PropertyConfigurator.configure(System.getProperty("log4j.configuration"))
        val ctx = AnnotationConfigApplicationContext(SpringConfig::class.java)
        // For the destroy method to work.
        ctx.registerShutdownHook()

        // Start the main game server
        val serverManager = ctx.getBean(ServerManager::class.java)

        try {
            //serverManager.startServers();
            serverManager.startServers(18090, 843, 50122)
        } catch (e: Exception) {
            LOG.error("Unable to start servers cleanly: {}", e)
        }

        println("Started servers")
        //startGames(ctx)
    }
}