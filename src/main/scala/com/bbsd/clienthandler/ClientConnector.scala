package com.bbsd.clienthandler
import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._
import com.bbsd.connectivity.api.websockets.chat.ChatMessage

class ClientConnector(user: Int, actorSystem: ActorSystem) extends Directives {

  private[this] val chatRoomActor = actorSystem.actorOf(Props(classOf[ClientConnectorActor], user))

  val wsHandler =
    Flow[Message]
      .collect {
        //          case tm: TextMessage => TextMessage(Source.single("Guten Tag, ") ++ tm.textStream)
        case conn: TextMessage => TextMessage.Strict("connected" )
        case tm: TextMessage => TextMessage.Strict("Guten Tag, " + tm.getStrictText)
      }

  def websocketFlow(user: String): Flow[Message, TextMessage.Strict, NotUsed] = //wsHandler /*
    Flow[Message]
      .collect {
        //          case tm: TextMessage => TextMessage(Source.single("Guten Tag, ") ++ tm.textStream)
        case conn: TextMessage => TextMessage.Strict("connected" )
        case tm: TextMessage => TextMessage.Strict("Guten Tag, " + tm.getStrictText)
      }
              //send messages to the actor, if send also UserLeft(user) before stream completes.


  def sendMessage(message: ChatMessage): Unit = chatRoomActor ! message

}

object ClientConnector {
  def apply(roomId: Int)(implicit actorSystem: ActorSystem) = new ClientConnector(roomId, actorSystem)
}