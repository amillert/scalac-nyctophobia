package pl.amillert
package nyctophobia
package services
package errors

import DomainErrors._

case object NoParametersProvided   extends ConfigParserError
case object NotADirectoryParameter extends ConfigParserError
case object UnknownConfigError     extends ConfigParserError
case object WrongThresholdValue    extends ConfigParserError

trait Arity
case object TooFew  extends Arity
case object TooMany extends Arity
case class WrongArityOfParameters(
    arity: Arity
  ) extends ConfigParserError

trait Range
case object TooSmall extends Range
case object TooBig   extends Range
case class ThresholdNotInRange(
    value: Range
  ) extends ConfigParserError
