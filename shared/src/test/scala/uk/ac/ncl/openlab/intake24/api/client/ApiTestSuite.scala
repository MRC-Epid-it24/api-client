package uk.ac.ncl.openlab.intake24.api.client

/**
  * IDE note: PlatformUtils is a platform-specific (JVM/JS) implementation of various system
  * functions. The actual implementation is in the jvm and js modules but the shared test code depends
  * on it creating a dependency that confuses IDEs such as IntelliJ IDEA.
  *
  * This is in fact a feature supported by the Scala.js SBT plugin and will work fine via SBT,
  * however the IDE will probably report an error along the lines of "Cannot resolve symbol PlatformUtils".
  *
  * Just ignore the error.
  */

import uk.ac.ncl.openlab.intake24.PlatformUtils
import org.scalatest.{Assertion, AsyncFunSuite}
import uk.ac.ncl.openlab.intake24.api.client.ApiError.{HttpError, NetworkError, ResultParseFailed}
import uk.ac.ncl.openlab.intake24.api.shared.ErrorDescription

import scala.concurrent.Future


trait ApiTestSuite extends AsyncFunSuite {
  /*
    def assertSuccessful[T](result: Either[ApiError, T]): T =
      result match {
        case Left(ResultParseFailed(cause: Throwable)) => fail(cause)

        case Left(ErrorParseFailed(httpCode, cause)) => fail(cause)

        case Left(RequestFailed(httpCode, errorCode, errorMessage)) => fail(s"API call failed with HTTP code $httpCode: $errorCode: $errorMessage")

        case Right(res) => res
      }

    def assertForbidden(result: Either[ApiError, Unit]) =
      result match {
        case Right(_) => fail("Expected response to be 403 Forbidden, but it was successful")
        case Left(RequestFailed(403, _, _)) => ()
        case Left(RequestFailed(httpCode, _, _)) => fail(s"Expected request to be 403 Forbidden, but got $httpCode instead")
        case Left(ResultParseFailed(cause: Throwable)) => fail(cause)
        case Left(ErrorParseFailed(httpCode, cause)) => fail(s"Could not parse error for code $httpCode", cause)
      }

    val apiBaseUrl = System.getProperty("apiBaseUrl", "http://localhost:9000")

    val signinClient = new SigninClientImpl(apiBaseUrl) */

  override implicit val executionContext = monix.execution.Scheduler.Implicits.global

  protected def assertRequestSuccessful[T](result: Either[ApiError, T]): Assertion = result match {
    case Right(_) => succeed
    case Left(ResultParseFailed(cause)) => fail(s"API request failed because the response body could not be parsed", cause)
    case Left(HttpError(code, errorDescription)) => errorDescription match {
      case Some(ErrorDescription(errorCode, errorMessage)) =>
        fail(s"API request failed with HTTP code $code: $errorCode: $errorMessage.")
      case None =>
        fail(s"API request failed with HTTP code $code. No further details were provided.")

    }
    case Left(NetworkError(cause)) =>
      fail("API request failed with IO exception", cause)
  }

  protected def assertHttpError[T](result: Either[ApiError, T], expectedCode: Int): Assertion = result match {
    case Left(HttpError(code, _)) if code == expectedCode => succeed
    case _ => fail(s"Expected HTTP error code $expectedCode. Got $result instead.")
  }

  protected def getEnvOrDie(variable: String): String = Option(PlatformUtils.getEnv(variable)) match {
    case Some(value) => value
    case None =>
      throw new RuntimeException(s"Please set $variable environment variable")
  }

  protected val apiBaseUrl = getEnvOrDie("INTAKE24_API_TEST_URL")
  protected val apiUser = getEnvOrDie("INTAKE24_API_TEST_USER")
  protected val apiPassword = getEnvOrDie("INTAKE24_API_TEST_PASSWORD")

}
