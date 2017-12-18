package uk.ac.ncl.openlab.intake24.api.client.services

import fr.hmil.roshttp.HttpRequest
import io.circe.Decoder
import uk.ac.ncl.openlab.intake24.api.client.ApiError

import scala.concurrent.Future

trait AuthRequestHandler {

  def sendWithAccessToken[T](request: HttpRequest)(implicit decoder: Decoder[T]): Future[Either[ApiError, T]]
}
