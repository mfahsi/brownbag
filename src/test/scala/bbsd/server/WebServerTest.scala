package bbsd.server

import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import org.scalatest.{Matchers, WordSpec}


class WebServerTest extends WordSpec with Matchers with ScalatestRouteTest with WebServer{

  "Websocket" should {
    "pull and push message" in {
      val wsClient = WSProbe()
      WS("/connect?name=CACIB", wsClient.flow) ~> routes ~>
        check {
          // check response for WS Upgrade headers
          isWebSocketUpgrade shouldEqual true

          // manually run a WS conversation
          wsClient.sendMessage("password=21335434")
          wsClient.expectMessage("connected")
        }
    }
  }
}
