package pl.amillert
package nyctophobia
package services

import scala.util.Try
import zio._

case class Config(
    inDir: String,
    outDir: String,
    brightnessThreshold: Int
  )

trait ConfigParser

object ConfigParser {
  import errors._
  import java.io._

  private type ConfigParserEnv = ConfigParser.Service

  private trait Service {
    def parse(args: List[String]): UIO[Config]
  }

  private val live: ConfigParserEnv = (args: List[String]) =>
    ZIO.fromEither {
      args match {
        case Nil =>
          Left(NoParametersProvided)
        case (_: String) :: (_: String) :: Nil | (_: String) :: Nil =>
          Left(WrongArityOfParameters)
        case (in: String) :: (out: String) :: (thresh: String) :: Nil =>
          Try(thresh.toInt).toEither match {
            case Right(threshold) =>
              if (!(new File(in)).isDirectory || !(new File(out).isDirectory))
                Left(NotADirectoryParameter)
              else
                Right(Config(in, out, threshold))
            case Left(x: java.lang.NumberFormatException) =>
              Left(WrongThresholdValue)
            case _ =>
              Left(UnknownConfigError)
          }
        case _ =>
          Left(UnknownConfigError)
      }
    }.orDie

  def parse(args: List[String]): UIO[Config] =
    live.parse(args)
}
