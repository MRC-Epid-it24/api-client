package uk.ac.ncl.openlab.intake24.api.client.roshttp.common

import java.io.IOException
import java.nio.ByteBuffer

import fr.hmil.roshttp.{HttpRequest, Method}
import fr.hmil.roshttp.body.BulkBodyPart
import fr.hmil.roshttp.exceptions.HttpException
import fr.hmil.roshttp.response.SimpleHttpResponse
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder}
import uk.ac.ncl.openlab.intake24.api.client.ApiError.NetworkError
import uk.ac.ncl.openlab.intake24.api.client.{ApiError, JsonCodecs}
import uk.ac.ncl.openlab.intake24.api.data.ErrorDescription

import scala.concurrent.Future
import monix.execution.Scheduler.Implicits.global

class EncodedJsonBody(json: String) extends BulkBodyPart {
  override def contentType: String = "application/json; charset=utf-8"

  override def contentData: ByteBuffer = ByteBuffer.wrap(json.getBytes("utf-8"))
}

trait HttpServiceUtils extends JsonCodecs {

  protected val apiBaseUrl: String

  lazy val apiBaseUrlNoSlash = apiBaseUrl.replaceAll("/+$", "")

  private def getUrl(endpoint: String) = apiBaseUrlNoSlash + "/" + endpoint.replaceAll("^/", "")

  protected def request(method: Method, endpoint: String, queryParameters: Seq[(String, String)] = Seq()): HttpRequest =
    HttpRequest(getUrl(endpoint))
      .withQueryParameters(queryParameters: _*)
      .withMethod(method)

  protected def requestWithBody[T](method: Method, endpoint: String, body: T, queryParameters: Seq[(String, String)] = Seq())(implicit encoder: Encoder[T]): HttpRequest =
    request(method, endpoint, queryParameters).withBody(encodeBody(body))

  protected def authRequest(endpoint: String, accessToken: String): HttpRequest =
    HttpRequest(getUrl(endpoint))
      //.withHeader("X-Auth-Token", accessToken)

  protected def authRequestWithResponse[T](method: Method, endpoint: String, authToken: String, queryParameters: Seq[(String, String)] = Seq())(implicit decoder: Decoder[T]): Future[Either[ApiError, T]] =
    authRequest(endpoint, authToken)
      .withMethod(method)

      .send()
      .map(decodeResponseBody[T])
      .recoverWith(recoverRequest)

  protected def authRequestWithBodyAndResponse[ReqT, RespT](method: Method, endpoint: String, authToken: String, body: ReqT, queryParameters: Seq[(String, String)] = Seq())(implicit decoder: Decoder[RespT], encoder: Encoder[ReqT]): Future[Either[ApiError, RespT]] =
    authRequest(endpoint, authToken)
      .withMethod(method)
      .withQueryParameters(queryParameters: _*)
      .withBody(encodeBody(body))
      .send()
      .map(decodeResponseBody[RespT])
      .recoverWith(recoverRequest)

  protected def authRequestWithBody[ReqT](method: Method, endpoint: String, authToken: String, body: ReqT, queryParameters: Seq[(String, String)] = Seq())(implicit encoder: Encoder[ReqT]): Future[Either[ApiError, Unit]] =
    authRequest(endpoint, authToken)
      .withMethod(method)
      .withQueryParameters(queryParameters: _*)
      .withBody(encodeBody(body))
      .send()
      .map(_ => Right(()))
      .recoverWith(recoverRequest)

  protected def encodeBody[T](body: T)(implicit encoder: Encoder[T]): EncodedJsonBody = new EncodedJsonBody(toJson(body))

  protected def recoverRequest[T]: PartialFunction[Throwable, Future[Either[ApiError, T]]] = {
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
