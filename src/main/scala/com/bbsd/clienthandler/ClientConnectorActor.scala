package com.bbsd.clienthandler

import akka.actor.{Actor, ActorRef}
import com.bbsd.connectivity.api.websockets.chat.{ChatMessage, IncomingMessage, SystemMessage, UserJoined, UserLeft}

class ClientConnectorActor(roomId: Int) extends Actor {

  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]

  override def receive: Receive = {
    case UserJoined(name, actorRef) =>
      participants += name -> actorRef
      broadcast(SystemMessage(s"User $name joined channel..."))
      println(s"User $name joined channel[$roomId]")

    case UserLeft(name) =>
      println(s"User $name left channel[$roomId]")
      broadcast(SystemMessage(s"User $name left channel[$roomId]"))
      participants -= name

    case msg: IncomingMessage =>
      broadcast(msg)
  }

  def broadcast(message: ChatMessage): Unit = participants.values.foreach(_ ! message)

}
