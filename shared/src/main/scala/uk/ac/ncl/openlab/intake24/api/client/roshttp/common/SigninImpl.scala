package uk.ac.ncl.openlab.intake24.api.client.roshttp.common

import fr.hmil.roshttp.{HttpRequest, Method}
import io.circe.generic.auto._
import monix.execution.Scheduler.Implicits.global
import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.client.services.common.AuthService
import uk.ac.ncl.openlab.intake24.api.data.{EmailCredentials, RefreshResult, SigninResult, SurveyAliasCredentials}

import scala.concurrent.Future

class SigninImpl(val apiBaseUrl: String) extends AuthService with HttpServiceUtils {

  def signin(credentials: EmailCredentials): Future[Either[ApiError, SigninResult]] = {
    request("/signin")
      .post(encodeBody(credentials))
      .map(decodeResponseBody[SigninResult])
      .recoverWith(recoverRequest)
  }

  def signinWithAlias(credentials: SurveyAliasCredentials): Future[Either[ApiError, SigninResult]] =
    request("/signin/alias")
      .post(encodeBody(credentials))
      .map(decodeResponseBody[SigninResult])
      .recoverWith(recoverRequest)

  def refresh(refreshToken: String): Future[Either[ApiError, RefreshResult]] =
    authRequestWithResponse[RefreshResult](Method.POST, "/refresh", refreshToken)
}
