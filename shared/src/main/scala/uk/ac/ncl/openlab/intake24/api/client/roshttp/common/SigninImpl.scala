package uk.ac.ncl.openlab.intake24.api.client.roshttp.common

import fr.hmil.roshttp.{HttpRequest, Method}
import io.circe.generic.auto._
import monix.execution.Scheduler.Implicits.global
import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.client.services.common.Signin
import uk.ac.ncl.openlab.intake24.api.shared.{EmailCredentials, RefreshResult, SigninResult, SurveyAliasCredentials}

import scala.concurrent.Future

class SigninImpl(val apiBaseUrl: String) extends Signin with HttpServiceUtils {

  def signin(credentials: EmailCredentials): Future[Either[ApiError, SigninResult]] = {
    HttpRequest(getUrl("/signin"))
      .post(encodeBody(credentials))
      .map(decodeResponseBody[SigninResult])
      .recoverWith(recover)
  }

  def signinWithAlias(credentials: SurveyAliasCredentials): Future[Either[ApiError, SigninResult]] =
    HttpRequest(getUrl("/signin/alias"))
      .post(encodeBody(credentials))
      .map(decodeResponseBody[SigninResult])
      .recoverWith(recover)

  def refresh(refreshToken: String): Future[Either[ApiError, RefreshResult]] =
    HttpRequest(getUrl("/refresh"))
      .withHeader("X-Auth-Token", refreshToken)
      .withMethod(Method.POST)
      .send()
      .map(decodeResponseBody[RefreshResult])
      .recoverWith(recover)
}
