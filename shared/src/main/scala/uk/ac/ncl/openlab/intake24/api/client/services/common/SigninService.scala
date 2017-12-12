package uk.ac.ncl.openlab.intake24.api.client.services.common

import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.data.{EmailCredentials, RefreshResult, SigninResult, SurveyAliasCredentials}

import scala.concurrent.Future

trait Signin {

  def signin(credentials: EmailCredentials): Future[Either[ApiError, SigninResult]]

  def signinWithAlias(credentials: SurveyAliasCredentials): Future[Either[ApiError, SigninResult]]

  def refresh(refreshToken: String): Future[Either[ApiError, RefreshResult]]
}
