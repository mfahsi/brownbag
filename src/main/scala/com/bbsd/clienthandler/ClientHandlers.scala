package com.bbsd.clienthandler

import akka.actor.ActorSystem

object ClientHandlers {
  var chatRooms: Map[Int, ClientConnector] = Map.empty[Int, ClientConnector]

  def findOrCreate(number: Int)(implicit actorSystem: ActorSystem): ClientConnector = chatRooms.getOrElse(number, createNewChatRoom(number))

  private def createNewChatRoom(number: Int)(implicit actorSystem: ActorSystem): ClientConnector = {
    val chatroom = ClientConnector(number)
    chatRooms += number -> chatroom
    chatroom
  }

}
