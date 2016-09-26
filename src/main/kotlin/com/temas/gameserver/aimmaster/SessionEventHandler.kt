package com.temas.gameserver.aimmaster

import com.google.protobuf.MessageLite
import com.google.protobuf.MessageLiteOrBuilder
import com.temas.aimaster.WorldStateProto
import io.nadron.app.Session
import io.nadron.communication.NettyMessageBuffer
import io.nadron.communication.DeliveryGuaranty.*
import io.nadron.event.Event
import io.nadron.event.Events
import io.nadron.event.impl.DefaultSessionEventHandler
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.util.concurrent.Executors

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 21.09.2016
 */
class SessionEventHandler(session : Session, val room: GameRoom) : DefaultSessionEventHandler(session) {


    override fun onDataIn(event: Event) {
        handleClientRequest(event.source)
    }

    fun convertToBuffer(obj : MessageLiteOrBuilder): ByteBuf {
        return Unpooled.wrappedBuffer((obj as MessageLite).toByteArray())
    }

    private fun handleClientRequest(source: Any) {
        println(source)

        val worldStateBuffer = NettyMessageBuffer()
        val responseBuilder = WorldStateProto.UpdateResponse.newBuilder()
        val targetInfoProto = responseBuilder.setTarget(
                WorldStateProto.TargetInfo.newBuilder().
                        setX(100).
                        setY(200).build()).
                build()
        val buffer = worldStateBuffer.writeObject({ convertToBuffer(it) }, targetInfoProto)
        room.sendBroadcast(Events.networkEvent(buffer, DeliveryGuarantyOptions.FAST))
    }

}