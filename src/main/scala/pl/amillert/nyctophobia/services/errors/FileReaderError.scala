package pl.amillert
package nyctophobia
package services
package errors

import DomainErrors._

case object LoadingFailedUnknown   extends FileReaderError
case object LoadingFailedWrongPath extends FileReaderError
