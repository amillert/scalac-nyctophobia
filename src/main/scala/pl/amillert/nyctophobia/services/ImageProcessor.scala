package pl.amillert
package nyctophobia
package services

import zio._

import java.io._

trait ImageProcessor

object ImageProcessor extends ops.ImageOps {
  import zio.console._

  private type ImageProcessorEnv = ImageProcessor.Service

  private trait Service {
    def parse(
        files: Array[File],
        config: Config
      ): ZIO[Console, IOException, Unit]
  }

  private val live: ImageProcessorEnv = (files: Array[File], config: Config) => {
    // array containing darkness values and saving descriptions
    val processed = for {
      file <- files
    } yield processImage(file, config)

    val darknessVals = processed.map(_._1)
    val evaluated    = evaluateModel(darknessVals)
    val saves        = ZIO.forkAll_(processed.map(_._2))

    evaluated *> saves
  }

  def parse(
      files: Array[File],
      config: Config
    ): ZIO[Console, IOException, Unit] =
    live.parse(files, config)
}
