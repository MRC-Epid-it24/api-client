package uk.ac.ncl.openlab.intake24.api.client.services.user

import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.data.{FoodDataForSurvey, LookupResult}

import scala.concurrent.Future

trait FoodDataService {

  def lookup(locale: String, description: String, existing: Seq[String] = Seq(),
             limit: Option[Int] = None, algorithm: Option[String] = None): Future[Either[ApiError, LookupResult]]

  def getFoodData(locale: String, code: String): Future[Either[ApiError, FoodDataForSurvey]]
}
