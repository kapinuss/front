package front

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import front.utils.Settings.{settings, content}

import scala.concurrent.{ExecutionContextExecutor, Future}

object Front extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val serverSource = Http().bind(interface = "0.0.0.0", port = settings.port)

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, content.getOrElse(settings.titles.head, "")))

    case HttpRequest(GET, Uri.Path("/about"), _, _, _) =>
      HttpResponse(entity = HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        s"<html><body>${settings.titles(1)}</body></html>"))

    case HttpRequest(GET, Uri.Path("/contacts"), _, _, _) =>
      HttpResponse(entity = HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        s"<html><body>${settings.titles(2)}</body></html>"))

    case r: HttpRequest =>
      r.discardEntityBytes()
      HttpResponse(404, entity = "")
  }

  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      connection handleWithSyncHandler requestHandler
    }).run()
}
