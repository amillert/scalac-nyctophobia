package pl.amillert
package nyctophobia
package services

import zio._

import java.io._
import javax.imageio.ImageIO

trait ImageProcessor

object ImageProcessor {
  import zio.console._

  type ImageProcessorEnv = ImageProcessor.Service

  trait Service {
    def parse(
        files: Array[File],
        config: Config
      ): ZIO[Console, Throwable, Unit]
  }

  val live: ImageProcessorEnv = (files: Array[File], config: Config) => {
    val processed =
      for {
        file <- files
        // avgLuminance: Int = mean(getLuminance(file))

      } yield processImage(file, config)

    val darks     = processed.map(_._2)
    val evaluated = evaluateModel(darks)
    val saves     = ZIO.forkAll_(processed.map(_._1))

    evaluated *> saves
  }

  private def processImage(
      file: File,
      config: Config
    ) = {
    val oldFileName           = file.getName
    val Array(prefix, suffix) = oldFileName.split('.')
    val luminance             = imageToLuminance(file)
    val darkness              = getDarkness(luminance, config.brightnessThreshold)
    val newFilename =
      s"${config.outDir}/${prefix}_${darkness}_$luminance.$suffix"

    save(file, new File(newFilename)) -> darkness
  }

  private val imageToLuminance: File => Int = mean _ compose getLuminance _

  private def getDarkness(luminance: Int, threshold: Int) =
    if (luminance < threshold) "dark"
    else "bright"

  private def getRGB(pixel: Int): (Int, Int, Int) =
    ((pixel >> 16) & 0xff, (pixel >> 8) & 0xff, pixel & 0xff)

  private def linearizeChannel(channel: Double): Double =
    if (channel <= 0.04045) channel / 12.92
    else scala.math.pow((channel + 0.055) / 1.055, 2.4)

  private def mean(pixels: Seq[Double]): Int =
    scala.math.round(pixels.foldRight(0.0)(_ + _) / pixels.size).toInt

  private def getLuminance(file: File) = {
    val img = ImageIO read file
    for {
      x <- 0 until img.getWidth
      y <- 0 until img.getHeight
      (r, g, b) = getRGB(img.getRGB(x, y))
    } yield perceivedLuminance(
      0.2126 * linearizeChannel(r / 255.0) +
        0.7152 * linearizeChannel(g / 255.0) +
        0.0722 * linearizeChannel(b / 255.0)
    )
  }

  private def perceivedLuminance(x: Double): Double =
    if (x <= 0.008856) x * 903.3
    else scala.math.pow(x, 1.0 / 3.0) * 116 - 16

  private def save(
      src: File,
      dst: File
    ): ZIO[Console, IOException, Unit] =
    ZIO.effectTotal {
      new FileOutputStream(dst)
        .getChannel
        .transferFrom(
          new FileInputStream(src).getChannel,
          0,
          Long.MaxValue
        )
    } *> putStrLn(s"[Info] Saved new file: ${dst.getName}")

  private def evaluateModel(darknessVals: Array[String]): Task[Unit] =
    Task {
      (for {
        x <- darknessVals
          .groupMapReduce(identity)(_ => 1)(_ + _)
          .map {
            case (cat, count) =>
              s"Ratio of $cat per all: ${count.toDouble / darknessVals.length}"
          }
      } yield x)
        .foreach(println(_))
    }

  def parse(
      files: Array[File],
      config: Config
    ): ZIO[Console, Throwable, Unit] =
    live.parse(files, config)
}
