package pl.amillert
package nyctophobia
package services

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatest.Inspectors._

class FileManagerFunSpec extends AnyFunSpec {
  import Setup._
  import errors._

  val ef = FileManager.FileReader.eitherFiles _

  describe("File Reader") {
    describe("when provided a valid Config") {
      describe("should ONLY succeed with the array of files") {
        describe("when both `in` and `out` directories are valid (which should be the case given validation in Config Parser)") {
          val res = ef(config).getOrElse(false)
          assert(res.isInstanceOf[Array[java.io.File]])
        }
      }

      // hypothetical; since ConfigParser should parse correctly
      describe("should ALWAYS fail") {
        describe("with `LoadingFailedWrongPath`") {
          ignore("when provided invalid input path") {
            forAll (List(configAllWrong, configWrongInDir)) { opt =>
              val res = ef(opt).getOrElse(Right(Array.empty[java.io.File]))
              res shouldEqual LoadingFailedWrongPath
            }
          }
        }
      }
    }
  }
}
