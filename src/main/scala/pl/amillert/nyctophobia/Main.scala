package pl.amillert
package nyctophobia

import zio._

object Main extends App {
  import services._

  import errors._

  private def program(args: List[String]) =
    for {
      config <- ConfigParser.parse(args)
      files <- FileManager.FileReader.read(config)
      _ <- ImageProcessor.parse(files, config)
    } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program(args).exitCode
}
