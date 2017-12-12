package uk.ac.ncl.openlab.intake24.api.client

import java.util.concurrent.atomic.AtomicReference

import uk.ac.ncl.openlab.intake24.api.client.ApiError.HttpError
import uk.ac.ncl.openlab.intake24.api.client.services.common.AuthService
import uk.ac.ncl.openlab.intake24.api.data.EmailCredentials

import scala.concurrent.{ExecutionContext, Future}

class AuthCacheImpl(authService: AuthService, val credentials: EmailCredentials) extends AuthCache with JsonCodecs {

  var accessToken = new AtomicReference[Option[String]](None)
  var refreshToken = new AtomicReference[Option[String]](None)

  def getRefreshToken()(implicit executionContext: ExecutionContext): Future[Either[ApiError, Unit]] =
    authService.signin(credentials).map(_.map {
      signinResult =>
        refreshToken.set(Some(signinResult.refreshToken))
    })

  def refreshAccessToken()(implicit executionContext: ExecutionContext): Future[Either[ApiError, String]] = refreshToken.get() match {
    case Some(token) =>
      authService.refresh(token).map(_.map {
        refreshResult =>
          accessToken.set(Some(refreshResult.accessToken))
          refreshResult.accessToken
      })

    case None =>
      for (_ <- getRefreshToken();
           token <- refreshAccessToken())
        yield token
  }

  def withAccessToken[T](block: String => Future[Either[ApiError, T]])(implicit executionContext: ExecutionContext): Future[Either[ApiError, T]] = {
    def refreshAndRetry = for (_ <- refreshAccessToken();
                               res <- withAccessToken(block))
      yield res

    accessToken.get() match {
      case Some(token) => block(token).flatMap {
        case Left(HttpError(401, _)) => refreshAndRetry
        case x => Future.successful(x)
      }
      case None => refreshAndRetry
    }
  }
}