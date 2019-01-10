package front

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import front.utils.Settings.settings

object Front extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val route =
    get {
      pathEndOrSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
          s"<html><body>index: {${System.currentTimeMillis()}}</body></html>"))
      } ~
        pathPrefix("chapter" / IntNumber) { chapter =>
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
            s"<html><body>chapter: $chapter, {${System.currentTimeMillis()}}</body></html>"))
        } ~
        pathPrefix("article" / IntNumber) { article =>
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
            s"<html><body>article: $article {${System.currentTimeMillis()}}</body></html>"))
        }
    } ~
      post {
        pathEndOrSingleSlash {
          complete(HttpEntity(ContentTypes.`application/json`,
            """{"test":"test"}"""))
        }
      }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", port = settings.port)

  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
