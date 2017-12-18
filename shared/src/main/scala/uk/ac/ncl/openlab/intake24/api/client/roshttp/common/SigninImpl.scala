package uk.ac.ncl.openlab.intake24.api.client.roshttp.common

import fr.hmil.roshttp.Method
import io.circe.generic.auto._
import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.client.services.RequestHandler
import uk.ac.ncl.openlab.intake24.api.client.services.common.AuthService
import uk.ac.ncl.openlab.intake24.api.data.{EmailCredentials, RefreshResult, SigninResult, SurveyAliasCredentials}

import scala.concurrent.Future

class SigninImpl(requestHandler: RequestHandler) extends AuthService with HttpServiceUtils {

  def signin(credentials: EmailCredentials): Future[Either[ApiError, SigninResult]] =
    requestHandler.send[SigninResult](requestWithBody(Method.POST, "/signin", credentials))

  def signinWithAlias(credentials: SurveyAliasCredentials): Future[Either[ApiError, SigninResult]] =
    requestHandler.send[SigninResult](requestWithBody(Method.POST, "/signin/alias", credentials))

  def refresh(refreshToken: String): Future[Either[ApiError, RefreshResult]] =
    requestHandler.send[RefreshResult](request(Method.POST, "/refresh").withHeader("X-Auth-Token", refreshToken))
}
