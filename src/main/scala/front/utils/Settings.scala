package front.utils

import com.typesafe.config.ConfigFactory
import front.Front.getClass
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

import scala.util.{Failure, Success, Try}

case class Settings(port: Int, titles: List[String])

object Settings {

  val settings: Settings =
    ConfigFactory.load("local.conf").withFallback(ConfigFactory.load).as[Settings]("front")

  val content: Map[String, String] = settings.titles.map(each => each -> read(each)).toMap[String, String]

  def read(str: String): String = {
    val source = Try(scala.io.Source.fromURL(getClass.getResource(s"/$str")))
    val text = source match {
      case Success(value) => try value.mkString finally value.close()
      case Failure(exception) => ""
    }
    text
  }
}