package pl.amillert
package nyctophobia
package services

import zio._

import java.io.File

trait FileReader

object FileReader {
  type FileReaderEnv = FileReader.Service

  trait Service {
    def read(config: Config): Task[Array[File]]
  }

  val live: FileReaderEnv = (config: Config) =>
    Task {
      new File(config.inDir).listFiles.filter(_.isFile)
    }

  def read(config: Config): Task[Array[File]] =
    live.read(config)
}
