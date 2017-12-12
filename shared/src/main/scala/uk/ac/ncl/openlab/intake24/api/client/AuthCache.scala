package uk.ac.ncl.openlab.intake24.api.client

import scala.concurrent.{ExecutionContext, Future}

trait AuthCache {

  def withAccessToken[T](block: String => Future[Either[ApiError, T]])(implicit executionContext: ExecutionContext): Future[Either[ApiError, T]]
}
