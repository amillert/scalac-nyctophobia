package pl.amillert
package nyctophobia
package services
package errors

import DomainErrors._

case object NoParametersProvided   extends ConfigParserError
case object NotADirectoryParameter extends ConfigParserError
case object UnknownConfigError     extends ConfigParserError
case object WrongArityOfParameters extends ConfigParserError
case object WrongThresholdValue    extends ConfigParserError
