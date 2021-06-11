package pl.amillert
package nyctophobia
package services

import org.scalatest.funspec.AnyFunSpec

class ConfigParserFunSpecTest extends AnyFunSpec {
  import Setup._
  import errors._

  private val ec = ConfigParser.eitherConfig _

  describe("Config Parser") {
    describe("when provided full list of arguments") {
      describe("should succeed ONLY") {
        describe("when two initial are valid directories' paths and the last one is threshold value in range [0, 100]") {
          assert(ec(args) == Right(config))
        }
      }
      describe("should fail") {
        describe("when initial arguments are not valid directories") {
          describe("with `NotADirectoryParameter`") {
            describe("when the first argument is not a valid directory") {
              assert(
                ec(argsWrongInDir) == Left(NotADirectoryParameter)
              )
            }
            describe("when the second argument is not a valid directory") {
              assert(
                ec(argsWrongOutDir) == Left(NotADirectoryParameter)
              )
            }
            describe("when both first and second arguments are not valid directories") {
              assert(
                ec(argsWrongBothDirs) == Left(NotADirectoryParameter)
              )
            }
          }

        }

        describe("when threshold argument can't be parsed to Int") {
          describe("with `WrongThresholdValue`") {
            describe("when Float provided instead") {
              assert(
                ec(argsWrongThresholdFormat) == Left(WrongThresholdValue)
              )
            }
            describe("when random String provided instead") {
              assert(
                ec(argsThresholdStr) == Left(WrongThresholdValue)
              )
            }
          }
        }

        describe("when threshold argument is not in range") {
          describe("with `ThresholdNotInRange(TooBig)`") {
            describe("when threshold argument exceeds the range maximum value") {
              assert(
                ec(argsThresholdTooBig) == Left(ThresholdNotInRange(TooBig))
              )
            }
          }
          describe("with `ThresholdNotInRange(TooSmall)`") {
            describe("when threshold argument below the range minimum value") {
              assert(
                ec(argsThresholdTooSmall) == Left(ThresholdNotInRange(TooSmall))
              )
            }
          }
        }
      }
    }

    describe("when NOT provided full list of arguments") {
      describe("should ALWAYS fail") {
        describe("when provided no arguments") {
          describe("with `NoParametersProvided`") {
            assert(
              ec(emptyList) == Left(NoParametersProvided)
            )
          }
        }

        describe("when not provided enough arguments") {
          describe("with `WrongArityArguments(TooFew)`") {
            describe("one argument") {
              assert(
                ec(strListOneElement) == Left(WrongArityOfParameters(TooFew))
              )
            }
            describe("two arguments") {
              assert(
                ec(strListTwoElements) == Left(WrongArityOfParameters(TooFew))
              )
            }
          }
        }

        describe("when provided too many arguments") {
          describe("with `WrongArityArguments(TooMany)`") {
            describe("e.g. four arguments") {
              describe("one additional provided as suffix") {
                assert(
                  ec(strListFourElementsSuffix) == Left(WrongArityOfParameters(TooMany))
                )
              }
              describe("one additional provided as prefix") {
                assert(
                  ec(strListFourElementsPrefix) == Left(WrongArityOfParameters(TooMany))
                )
              }
            }
          }
        }

      }
    }
  }
}
