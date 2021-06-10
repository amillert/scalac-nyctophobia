package pl.amillert
package nyctophobia
package services

import zio._
import zio.console._
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

object ConfigParserZIOSpec extends DefaultRunnableSpec {
  import Setup._

  def spec = suite("Config Parser should")(
    testM("succeed when provided a correct full arguments list") {
      for {
        confRes <- ConfigParser.parse(args)
        _       <- zio.console.putStrLn(s"\nRead config: $confRes\n")
      } yield assert(confRes)(equalTo(config))
    }
  )
}
