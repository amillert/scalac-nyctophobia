package pl.amillert
package nyctophobia
package services

import org.scalatest.flatspec.AnyFlatSpec

class ConfigParserFlatSpecTest extends AnyFlatSpec {
  import Setup._
  import errors._

  behavior of "Config Parser"

  it should "fail with WrongArityArguments error when not provided enough arguments" in {
    assert(
      ConfigParser.eitherConfig(strListOneElement) == Left(
        WrongArityOfParameters
      )
    )
  }

  it should "fail with WrongArityArguments error when not provided enough arguments" in {
    assert(
      ConfigParser.eitherConfig(strListOneElement) == Left(
        WrongArityOfParameters
      )
    )
  }

}
