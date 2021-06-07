package pl.amillert
package nyctophobia
package services

import java.io.File
import zio._

trait FileReader
object FileReader {
  type FileReaderEnv = FileReader.Service

  trait Service {
    def read(config: Config): Task[Array[File]]
  }

  val live: FileReaderEnv = new Service {
    override def read(config: Config): Task[Array[File]] = Task {
      (new File(config.inDir)).listFiles.filter(_.isFile)
    }
  }

  def read(config: Config): Task[Array[File]] =
    live.read(config)
}
