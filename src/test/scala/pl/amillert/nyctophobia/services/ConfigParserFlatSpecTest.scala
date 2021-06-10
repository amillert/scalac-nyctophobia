package pl.amillert
package nyctophobia
package services

import org.scalatest.flatspec.AnyFlatSpec

class ConfigParserFlatSpecTest extends AnyFlatSpec {
  import Setup._
  import errors._

  behavior of "Config Parser"

  it should "fail with NoParametersProvided error when not provided any arguments" in {
    assert(
      ConfigParser.eitherConfig(emptyList) == Left(
        NoParametersProvided
      )
    )
  }

  it should "fail with WrongArityArguments(TooFew) error when not provided enough arguments - one argument" in {
    assert(
      ConfigParser.eitherConfig(strListOneElement) == Left(
        WrongArityOfParameters(TooFew)
      )
    )
  }

  it should "fail with WrongArityArguments(TooFew) error when not provided enough arguments - two arguments" in {
    assert(
      ConfigParser.eitherConfig(strListTwoElements) == Left(
        WrongArityOfParameters(TooFew)
      )
    )
  }

  it should "fail with WrongArityArguments(TooMany) error when not provided too many arguments - four arguments; wrong as suffix" in {
    assert(
      ConfigParser.eitherConfig(strListFourElementsSuffix) == Left(
        WrongArityOfParameters(TooMany)
      )
    )
  }

  it should "fail with WrongArityArguments(TooMany) error when not provided too many arguments - four arguments; wrong as prefix" in {
    assert(
      ConfigParser.eitherConfig(strListFourElementsPrefix) == Left(
        WrongArityOfParameters(TooMany)
      )
    )
  }

  it should "fail with NotADirectoryParameter error when either of the initial arguments is not a directory - first not a directory" in {
    assert(
      ConfigParser.eitherConfig(argsWrongInDir) == Left(
        NotADirectoryParameter
      )
    )
  }

  it should "fail with NotADirectoryParameter error when either of the initial arguments is not a directory - second not a directory" in {
    assert(
      ConfigParser.eitherConfig(argsWrongOutDir) == Left(
        NotADirectoryParameter
      )
    )
  }

  it should "fail with NotADirectoryParameter error when either of the initial arguments is not a directory - both not directories" in {
    assert(
      ConfigParser.eitherConfig(argsWrongBothDirs) == Left(
        NotADirectoryParameter
      )
    )
  }

  it should "fail with WrongThresholdValue error when the threshold argument can't be parsed to Int - Float as String" in {
    assert(
      ConfigParser.eitherConfig(argsWrongThresholdFormat) == Left(
        WrongThresholdValue
      )
    )
  }

  it should "fail with WrongThresholdValue error when the threshold argument can't be parsed to Int - String value" in {
    assert(
      ConfigParser.eitherConfig(argsThresholdStr) == Left(
        WrongThresholdValue
      )
    )
  }

  it should "fail with TooBigThresholdValue error when the threshold argument exceeds the range maximum value" in {
    assert(
      ConfigParser.eitherConfig(argsThresholdTooBig) == Left(
        ThresholdNotInRange(TooBig)
      )
    )
  }

  it should "fail with TooBigThresholdValue error when the threshold argument below the range minimum value" in {
    assert(
      ConfigParser.eitherConfig(argsThresholdTooSmall) == Left(
        ThresholdNotInRange(TooSmall)
      )
    )
  }
}
