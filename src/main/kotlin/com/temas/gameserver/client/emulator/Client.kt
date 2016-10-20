package com.temas.gameserver.client.emulator

import com.google.protobuf.MessageLite
import com.temas.aimaster.WorldStateProto
import io.nadron.client.app.Session
import io.nadron.client.app.impl.SessionFactory
import io.nadron.client.communication.NettyMessageBuffer
import io.nadron.client.event.Event
import io.nadron.client.event.impl.AbstractSessionEventHandler
import io.nadron.client.util.LoginHelper
import io.netty.buffer.ByteBuf
import java.util.concurrent.Executors

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 21.09.2016
 */

fun main(args: Array<String>) {
    Client().start()
}

private val taskExecutor = Executors.newSingleThreadScheduledExecutor()



class Client {

    private class InBoundHandler(session: Session) : AbstractSessionEventHandler(session) {
        private var inPacketCount = 0

        private val prototype: MessageLite = WorldStateProto.UpdateResponse.getDefaultInstance()

        override fun onDataIn(event: Event) {
            inPacketCount++
            val buffer = event.source as NettyMessageBuffer
            val response = buffer.readObject { convertToProto(it) } as WorldStateProto.UpdateResponse
            val worldState = response.target
            println("Received state x= ${worldState.x}, y=${worldState.y} cnt=$inPacketCount")
        }

        fun convertToProto(msg: ByteBuf): MessageLite {
            val array: ByteArray
            val offset: Int
            val length = msg.readableBytes()
            if (msg.hasArray()) {
                array = msg.array()
                offset = msg.arrayOffset() + msg.readerIndex()
            } else {
                array = ByteArray(length)
                msg.getBytes(msg.readerIndex(), array, 0, length)
                offset = 0
            }

            return prototype.parserForType.parseFrom(array, offset, length)
        }
    }


    fun start() {
        val builder = LoginHelper.LoginBuilder().
                username("test" ).
                password("pass").
                connectionKey("defaultRoom").
                nadronTcpHostName("localhost").
                tcpPort(18090).
                nadronUdpHostName("localhost").
                udpPort(50122)

        val loginHelper = builder.build()
        val sessionFactory = SessionFactory(loginHelper)
        val session = sessionFactory.createSession()
        val handler = InBoundHandler(session)
        session.addHandler(handler)
        sessionFactory.connectSession(session)
    }
}