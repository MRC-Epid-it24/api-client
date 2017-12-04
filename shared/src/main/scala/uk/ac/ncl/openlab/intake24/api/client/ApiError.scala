package uk.ac.ncl.openlab.intake24.api.client

import uk.ac.ncl.openlab.intake24.api.shared.ErrorDescription

sealed trait ApiError

object ApiError {

  case class ResultParseFailed(cause: Throwable) extends ApiError

  case class HttpError(httpCode: Int, errorDescription: Option[ErrorDescription]) extends ApiError

  case class NetworkError(cause: Throwable) extends ApiError
}
