package pl.amillert
package nyctophobia
package services

import org.scalatest.funspec.AnyFunSpec

class ConfigParserFlatSpecTest extends AnyFunSpec {
  import Setup._
  import errors._

  describe("Config Parser") {
    describe("when provided full list of arguments") {
      describe("should succeed") {
        describe(
          "when two initial are valid directories' paths and the last one is threshold value in range [0, 100]"
        ) {
          assert(ConfigParser.eitherConfig(args) == Right(config))
        }
      }

      describe("should fail") {
        describe("when provided no arguments") {
          describe("with `NoParametersProvided`") {
            assert(
              ConfigParser.eitherConfig(emptyList) == Left(
                NoParametersProvided
              )
            )
          }
        }
        describe("when not provided enough arguments") {
          describe("with `WrongArityArguments(TooFew)`") {
            describe("one argument") {
              assert(
                ConfigParser.eitherConfig(strListOneElement) == Left(
                  WrongArityOfParameters(TooFew)
                )
              )

            }
            describe("two arguments") {
              assert(
                ConfigParser.eitherConfig(strListTwoElements) == Left(
                  WrongArityOfParameters(TooFew)
                )
              )

            }
          }
        }

        describe("when provided too many arguments") {
          describe("with `WrongArityArguments(TooMany)`") {
            describe("e.g. four arguments") {
              describe("one additional provided as suffix") {
                assert(
                  ConfigParser.eitherConfig(
                    strListFourElementsSuffix
                  ) == Left(WrongArityOfParameters(TooMany))
                )
              }
              describe("one additional provided as prefix") {
                assert(
                  ConfigParser.eitherConfig(
                    strListFourElementsPrefix
                  ) == Left(WrongArityOfParameters(TooMany))
                )
              }
            }
          }
        }

        describe("when initial arguments are not valid directories") {
          describe("with `NotADirectoryParameter`") {
            describe("when the first argument is not a valid directory") {
              assert(
                ConfigParser.eitherConfig(argsWrongInDir) == Left(
                  NotADirectoryParameter
                )
              )
            }
            describe("when the second argument is not a valid directory") {
              assert(
                ConfigParser.eitherConfig(argsWrongOutDir) == Left(
                  NotADirectoryParameter
                )
              )
            }
            describe(
              "when both first and second arguments are not valid directories"
            ) {
              assert(
                ConfigParser.eitherConfig(argsWrongBothDirs) == Left(
                  NotADirectoryParameter
                )
              )
            }
          }

        }

        describe("when threshold argument can't be parsed to Int") {
          describe("with `WrongThresholdValue`") {
            describe("when Float provided instead") {
              assert(
                ConfigParser.eitherConfig(argsWrongThresholdFormat) == Left(
                  WrongThresholdValue
                )
              )
            }
            describe("when random String provided instead") {
              assert(
                ConfigParser.eitherConfig(argsThresholdStr) == Left(
                  WrongThresholdValue
                )
              )
            }
          }
        }

        describe("when threshold argument is not in range") {
          describe("with `ThresholdNotInRange(TooBig)`") {
            describe(
              "when threshold argument exceeds the range maximum value"
            ) {
              assert(
                ConfigParser.eitherConfig(argsThresholdTooBig) == Left(
                  ThresholdNotInRange(TooBig)
                )
              )
            }
          }
          describe("with `ThresholdNotInRange(TooSmall)`") {
            describe(
              "when threshold argument below the range minimum value"
            ) {
              assert(
                ConfigParser.eitherConfig(argsThresholdTooSmall) == Left(
                  ThresholdNotInRange(TooSmall)
                )
              )
            }
          }
        }
      }
    }
  }
}
