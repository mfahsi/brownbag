package bbsd.server

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives
import akka.stream.scaladsl.{Flow, Source}
import com.bbsd.websockets.services.{EchoService}


trait WebServer extends Directives {
  val wsHandler =
    Flow[Message]
      .collect {
        //          case tm: TextMessage => TextMessage(Source.single("Guten Tag, ") ++ tm.textStream)
        case conn: TextMessage => TextMessage.Strict("connected" )
        case tm: TextMessage => TextMessage.Strict("Guten Tag, " + tm.getStrictText)
        case _ => TextMessage.Strict("ünknown");
      }
  val route =  EchoService.route

  val routes =
    concat(
      pathSingleSlash {
        get {println("request ")

          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Hi there!</h1>"))
        }
      },
      path("connect") {
        get {
          println(s"connect ")
          handleWebSocketMessages(wsHandler)
        }
      },
      path("dealer") {
        get {
          handleWebSocketMessages(wsHandler)
        }
      },
      path("query") {
        get {
          extractHost { host =>
            complete(HttpEntity(s"The host is: $host"))
          }
        }
      },
      path("protocol") {
        get {
          extract(_.request.protocol) { protocol =>
            complete(HttpEntity(s"The status is: ${protocol}"))
          }
        }
      },
      EchoService.route
    )

  def broadcast(name: String): Flow[Message, Message, Any] = {
    Flow[Message].mapConcat {
      case tm: TextMessage =>
        TextMessage(Source.single(name+"::") ++ tm.textStream) :: Nil
    }

  }
}
