package uk.ac.ncl.openlab.intake24.api.client.roshttp.user

import fr.hmil.roshttp.Method
import io.circe.generic.auto._
import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.client.roshttp.common.HttpServiceUtils
import uk.ac.ncl.openlab.intake24.api.client.services.RequestHandler
import uk.ac.ncl.openlab.intake24.api.client.services.user.FoodDataService
import uk.ac.ncl.openlab.intake24.api.data.LookupResult

import scala.concurrent.Future

class FoodDataImpl(val requestHandler: RequestHandler) extends FoodDataService  {
  def lookup(locale: String, description: String, existing: Seq[String], limit: Option[Int], algorithm: Option[String]): Future[Either[ApiError, LookupResult]] = {
    val queryParameters =
      Seq("desc" -> description) ++ existing.map("existing" -> _) ++ Seq(limit.map("limit" -> _.toString), algorithm.map("alg" -> _)).flatten

    //requestHandler.sendWithAccessToken(request(Method.GET,  s"/foods/$locale/lookup", queryParameters))
    null



  }
}
