package uk.ac.ncl.openlab.intake24.api.client.roshttp.common

import java.io.IOException
import java.nio.ByteBuffer

import fr.hmil.roshttp.body.BulkBodyPart
import fr.hmil.roshttp.exceptions.HttpException
import fr.hmil.roshttp.response.SimpleHttpResponse
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder}
import uk.ac.ncl.openlab.intake24.api.client.ApiError.NetworkError
import uk.ac.ncl.openlab.intake24.api.client.{ApiError, JsonCodecs}
import uk.ac.ncl.openlab.intake24.api.shared.ErrorDescription

import scala.concurrent.Future

import monix.execution.Scheduler.Implicits.global

class EncodedJsonBody(json: String) extends BulkBodyPart {
  override def contentType: String = "application/json; charset=utf-8"

  override def contentData: ByteBuffer = ByteBuffer.wrap(json.getBytes("utf-8"))
}

trait HttpServiceUtils extends JsonCodecs {

  protected val apiBaseUrl: String

  lazy val apiBaseUrlNoSlash = apiBaseUrl.replaceAll("/+$", "")

  protected def getUrl(endpoint: String): String = apiBaseUrlNoSlash + "/" + endpoint.replaceAll("^/", "")

  protected def encodeBody[T](body: T)(implicit encoder: Encoder[T]): EncodedJsonBody = new EncodedJsonBody(toJson(body))

  protected def recover[T]: PartialFunction[Throwable, Future[Either[ApiError, T]]] = {
    case HttpException(e: SimpleHttpResponse) =>
      Future {
        decodeError(e)
      }
    case e: IOException =>
      Future {
        Left(NetworkError(e))
      }
  }

  protected def decodeResponseBody[T](response: SimpleHttpResponse)(implicit decoder: Decoder[T]): Either[ApiError, T] =
    fromJson[T](response.body) match {
      case Right(result) => Right(result)
      case Left(e) => Left(ApiError.ResultParseFailed(e))
    }

  protected def decodeError[T](response: SimpleHttpResponse): Either[ApiError, T] = response.statusCode match {
    case 401 => Left(ApiError.HttpError(401, None))
    case 403 => Left(ApiError.HttpError(403, None))
    case code =>
      fromJson[ErrorDescription](response.body) match {
        case Right(errorDescription) => Left(ApiError.HttpError(code, Some(errorDescription)))
        case Left(e) => Left(ApiError.HttpError(code, None))
      }
  }


}
