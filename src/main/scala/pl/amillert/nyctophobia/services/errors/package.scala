package pl.amillert
package nyctophobia
package services

package object errors {
  trait DomainErrors extends Throwable

  object DomainErrors {
    trait FileReaderError   extends DomainErrors
    trait ConfigParserError extends DomainErrors
  }
}
