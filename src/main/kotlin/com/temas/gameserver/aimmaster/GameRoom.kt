package com.temas.gameserver.aimmaster

import com.google.protobuf.MessageLite
import com.google.protobuf.MessageLiteOrBuilder
import com.temas.aimaster.WorldStateProto
import io.nadron.app.PlayerSession
import io.nadron.app.impl.GameRoomSession
import io.nadron.communication.NettyMessageBuffer
import io.nadron.event.Events
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 30.08.2016
 */

private var outPacketCount = 0

class GameRoom(builder: GameRoomSessionBuilder) : GameRoomSession(builder) {

    companion object {
        private val LOG = LoggerFactory.getLogger(GameRoom::class.java)
        private val updateThreadGroup = ThreadGroup("Update thread group")
        private val scheduler = Executors.newSingleThreadScheduledExecutor { r -> Thread(updateThreadGroup, r) }
        private val UPDATE_RATE = 1 //times per second
        private val UPDATE_DELAY = 1000L / UPDATE_RATE
    }

    var updateStarted = false

    override fun onLogin(playerSession: PlayerSession) {
        LOG.debug("Player with name: '${playerSession.player.name}' was logged in")
        playerSession.addHandler(SessionEventHandler(playerSession, this))
        if (!updateStarted) {
            startUpdateSchedule()
            updateStarted = true
        }
    }

    private fun startUpdateSchedule() {
        scheduler.scheduleWithFixedDelay(updateTask, 0, UPDATE_DELAY, TimeUnit.MILLISECONDS)
    }

    private val updateTask = Runnable {
        if (getSessions().isEmpty()) return@Runnable

        val responseBuilder = WorldStateProto.UpdateResponse.newBuilder()
        val targetInfoProto = responseBuilder.setTarget(
                WorldStateProto.TargetInfo.newBuilder().
                        setX(100).
                        setY(200).build()).
                build()
        getSessions().forEach {
            try {
                val worldStateBuffer = NettyMessageBuffer()
                val buffer = worldStateBuffer.writeObject({ convertToBuffer(it) }, targetInfoProto)
                val event = Events.networkEvent(buffer, io.nadron.communication.DeliveryGuaranty.DeliveryGuarantyOptions.FAST)
                it.onEvent(event)
                ++outPacketCount

                println("Send data packet number = $outPacketCount")
            } catch (ex: Exception) {
                LOG.error("Error during update task", ex)
            }
        }
    }


    fun convertToBuffer(obj : MessageLiteOrBuilder): ByteBuf {
        return Unpooled.wrappedBuffer((obj as MessageLite).toByteArray())
    }
}