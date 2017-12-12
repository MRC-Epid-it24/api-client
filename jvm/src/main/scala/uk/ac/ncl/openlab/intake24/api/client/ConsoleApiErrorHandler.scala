package uk.ac.ncl.openlab.intake24

import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.client.ApiError.{HttpError, NetworkError, ResultParseFailed}
import uk.ac.ncl.openlab.intake24.api.data.ErrorDescription

trait ConsoleApiErrorHandler {

  def checkApiError(result: Either[ApiError, Unit]) = result match {
    case Right(()) => println("Success!")
    case Left(NetworkError(throwable)) =>
      println("API request failed due to a network error")
      throwable.printStackTrace()
    case Left(HttpError(httpCode, errorDescription)) =>
      println("API request failed!")
      println(s"  HTTP code: $httpCode")
      errorDescription match {
        case Some(ErrorDescription(errorCode, errorMessage)) =>

          println(s"  Error code: $errorCode")
          println(s"  Error message: $errorMessage")
        case None =>
          println("API request failed (no additional error information available)")
      }
    case Left(ResultParseFailed(cause: Throwable)) =>
      println("API call succeeded, but failed to parse result!")
      println(s"  Parser error: ${cause.getClass.getSimpleName}: ${cause.getMessage}")
  }
}
