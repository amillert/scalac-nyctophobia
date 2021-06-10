package pl.amillert
package nyctophobia
package services

import zio._

case class Config(
    inDir: String,
    outDir: String,
    brightnessThreshold: Int
  )

trait ConfigParser

object ConfigParser {
  type ConfigParserEnv = ConfigParser.Service

  case object FailConfigParserMissingParameter extends Throwable

  trait Service {
    def parse(args: List[String]): Task[Config]
  }

  val live: ConfigParserEnv = (args: List[String]) =>
    ZIO
      .fromOption {
        args match {
          case in :: out :: thresh :: Nil =>
            Some(Config(in, out, thresh.toInt))
          case _ =>
            None: Option[Config]
        }
      }
      .orDieWith(_ => FailConfigParserMissingParameter)

  def parse(args: List[String]): Task[Config] =
    live.parse(args)
}
