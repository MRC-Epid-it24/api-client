package uk.ac.ncl.openlab.intake24.api.client.roshttp.user

import fr.hmil.roshttp.Method
import io.circe.generic.auto._
import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.client.roshttp.common.HttpServiceUtils
import uk.ac.ncl.openlab.intake24.api.client.services.AuthRequestHandler
import uk.ac.ncl.openlab.intake24.api.client.services.user.FoodDataService
import uk.ac.ncl.openlab.intake24.api.data.{FoodDataForSurvey, LookupResult}

import scala.concurrent.Future

class FoodDataImpl(val requestHandler: AuthRequestHandler) extends FoodDataService with HttpServiceUtils {
  def lookup(locale: String, description: String, existing: Seq[String], limit: Option[Int], algorithm: Option[String]): Future[Either[ApiError, LookupResult]] = {
    val queryParameters =
      Seq("desc" -> description) ++ existing.map("existing" -> _) ++ Seq(limit.map("limit" -> _.toString), algorithm.map("alg" -> _)).flatten

    requestHandler.sendWithAccessToken[LookupResult](request(Method.GET, s"/user/foods/$locale/lookup", queryParameters))
  }

  def getFoodData(locale: String, code: String): Future[Either[ApiError, FoodDataForSurvey]] =
    requestHandler.sendWithAccessToken[FoodDataForSurvey](request(Method.GET, s"/user/foods/$locale/$code"))
}
