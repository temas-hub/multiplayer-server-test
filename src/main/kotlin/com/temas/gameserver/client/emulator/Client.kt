package com.temas.gameserver.client.emulator

import com.google.protobuf.MessageLite
import com.temas.aimaster.WorldStateProto
import io.nadron.client.app.Session
import io.nadron.client.app.impl.SessionFactory
import io.nadron.client.communication.DeliveryGuaranty.*
import io.nadron.client.communication.NettyMessageBuffer
import io.nadron.client.event.Event
import io.nadron.client.event.Events
import io.nadron.client.event.impl.AbstractSessionEventHandler
import io.nadron.client.util.LoginHelper
import io.netty.buffer.ByteBuf
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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

        private val prototype: MessageLite = WorldStateProto.UpdateResponse.getDefaultInstance()

        override fun onDataIn(event: Event) {
            val buffer = event.source as NettyMessageBuffer
            val response = buffer.readObject { convertToProto(it) } as WorldStateProto.UpdateResponse
            val worldState = response.target
            println("Received state x= ${worldState.x}, y=${worldState.y}")
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
        val session = sessionFactory.createAndConnectSession()

        val task = Runnable {
            val messageBuffer = NettyMessageBuffer()
            messageBuffer.writeInt(1)
            messageBuffer.writeInt(2)
            val event = Events.networkEvent(messageBuffer, DeliveryGuarantyOptions.FAST)
            session.onEvent(event)
        }

        taskExecutor.scheduleAtFixedRate(task, 2000, 200, TimeUnit.MILLISECONDS)
        val handler = InBoundHandler(session)
        session.addHandler(handler)
    }
}