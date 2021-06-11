package pl.amillert
package nyctophobia
package services
package ops

import javax.imageio.ImageIO
import java.io._

import zio._

trait ImageOps {
  import zio.console._

  import FileManager.FileWriter

  protected def processImage(
      file: File,
      config: Config
    ): (String, ZIO[Console, IOException, Unit]) = {
    val oldFileName           = file.getName
    val Array(prefix, suffix) = oldFileName.split('.')
    val luminance             = imageToLuminance(file)
    val darkness              = getDarkness(luminance, config.brightnessThreshold)
    val newFilename =
      s"${config.outDir}/${prefix}_${darkness}_$luminance.$suffix"

    darkness -> FileWriter.write(file, new File(newFilename))
  }

  protected def evaluateModel(darknessVals: Array[String]): UIO[Unit] =
    UIO.succeed {
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

  private val imageToLuminance: File => Int = mean _ compose getLuminance

  private def mean(pixels: Seq[Double]) =
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

  private def getDarkness(luminance: Int, threshold: Int) =
    if (luminance < threshold) "dark"
    else "bright"

  private def getRGB(pixel: Int): (Int, Int, Int) =
    ((pixel >> 16) & 0xff, (pixel >> 8) & 0xff, pixel & 0xff)

  private def perceivedLuminance(x: Double) =
    if (x <= 0.008856) x * 903.3
    else scala.math.pow(x, 1.0 / 3.0) * 116 - 16

  private def linearizeChannel(channel: Double) =
    if (channel <= 0.04045) channel / 12.92
    else scala.math.pow((channel + 0.055) / 1.055, 2.4)
}
