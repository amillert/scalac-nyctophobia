package pl.amillert
package nyctophobia
package services

import java.io._
import javax.imageio.ImageIO
import zio._

trait ImageProcessor
object ImageProcessor {
  import zio.console._

  type ImageProcessorEnv = ImageProcessor.Service

  trait Service {
    def parse(file: File, config: Config): ZIO[Console, IOException, Unit]
  }

  val live: ImageProcessorEnv = new Service {
    override def parse(
        file: File,
        config: Config
      ): ZIO[Console, IOException, Unit] = {
      val img = ImageIO read file
      val pixels =
        for {
          x <- (0 until img.getWidth)
          y <- (0 until img.getHeight)
          (r, g, b) = getRGB(img.getRGB(x, y))
        } yield perceivedLuminance(
          0.2126 * linearizeChannel(r / 255.0) +
          0.7152 * linearizeChannel(g / 255.0) +
          0.0722 * linearizeChannel(b / 255.0)
        )

      val avgLuminance = mean(pixels)

      val newFileName =
        generateNewFileName(file, avgLuminance, config.brightnessThreshold)

      val dst = new File(s"${config.outDir}/$newFileName")

      save(file, dst)
    }
  }

  private def generateNewFileName(
      file: File,
      avgLuminance: Long,
      threshold: Int
    ): String = {
    val oldFileName = file.getName
    val parDir      = file.getParent

    val Array(prefix, suffix) = oldFileName.split('.')

    val darkness = avgLuminance match {
      case luminance if luminance < threshold => "dark"
      case _                                  => "bright"
    }

    s"${prefix}_${darkness}_$avgLuminance.$suffix"
  }

  private def getRGB(pixel: Int): (Int, Int, Int) =
    ((pixel >> 16) & 0xff, (pixel >> 8) & 0xff, pixel & 0xff)

  private def linearizeChannel(channel: Double): Double =
    if (channel <= 0.04045) channel / 12.92
    else scala.math.pow((channel + 0.055) / 1.055, 2.4)

  private def mean(pixels: Seq[Double]): Int =
    scala.math.round(pixels.foldRight(0.0)(_ + _) / pixels.size).toInt

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

  def parse(file: File, config: Config): ZIO[Console, IOException, Unit] =
    live.parse(file, config)
}
