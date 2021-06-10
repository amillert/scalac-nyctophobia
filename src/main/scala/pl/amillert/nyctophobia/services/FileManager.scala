package pl.amillert
package nyctophobia
package services

import zio._

import java.io._
import scala.util.Try

trait FileManger

object FileManager {
  object FileReader {
    import errors._

    private type FileReaderEnv = FileReader.Service

    private trait Service {
      def read(config: Config): UIO[Array[File]]
    }

    private val live: FileReaderEnv = (config: Config) =>
      ZIO.fromEither {
        Try(
          new File(config.inDir).listFiles.filter(_.isFile)
        ).toEither match {
          case Right(files: Array[File]) =>
            Right(files)
          case Left(_: java.lang.NullPointerException) =>
            Left(LoadingFailedWrongPath)
          case Left(_: Throwable) =>
            Left(LoadingFailedUnknown)
        }
      }.orDie

    def read(config: Config): UIO[Array[File]] =
      live.read(config)
  }

  object FileWriter {
    import zio.console._

    private type FileWriterEnv = FileWriter.Service

    private trait Service {
      def write(
          src: File,
          dst: File
        ): ZIO[Console, IOException, Unit]
    }

    private val live: FileWriterEnv = (src: File, dst: File) =>
      ZIO.effectTotal {
        new FileOutputStream(dst)
          .getChannel
          .transferFrom(
            new FileInputStream(src).getChannel,
            0,
            Long.MaxValue
          )
      } *> putStrLn(s"[Info] Saved new file: ${dst.getName}")

    def write(src: File, dst: File): ZIO[Console, IOException, Unit] =
      live.write(src, dst)
  }
}
