package bbsd

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import bbsd.Boot.Configuration
import bbsd.server.WebServer
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.{Failure, Success}

object Boot extends App with WebServer {

  implicit val system = ActorSystem("dealer-webconnector")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val hostname : String = "127.0.0.1"
  val port : Integer = Integer.parseInt(if(Configuration.serverPort == null) "9090" else Configuration.serverPort);
  val bindingFuture = Http().bindAndHandle(routes, hostname, port)

  bindingFuture.onComplete {
    case Success(binding) ⇒
      println(s"Server bbsd is running on $hostname:$port")
    case Failure(e) ⇒
      println(s"Binding failed with ${e.getMessage}")
      system.terminate()
  }

  object Configuration {
    private val env = System.getenv("local")

    val config: Config = env match {
      case null => ConfigFactory.load()
      case _ => ConfigFactory.load(env)
    }

    lazy val secret: String = config.getString("my.secret.value")
    lazy val serverPort: String = config.getString("app.port")
  }

}



