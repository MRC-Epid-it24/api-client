package uk.ac.ncl.openlab.intake24.api.client.services.admin

import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.data.admin.{FoodRecord, LocalFoodRecordUpdate}

import scala.concurrent.Future

trait FoodsAdminService {
  def getFoodRecord(accessToken: String, foodCode: String, locale: String): Future[Either[ApiError, FoodRecord]]

  def updateLocalFoodRecord(accessToken: String, foodCode: String, locale: String, update: LocalFoodRecordUpdate): Future[Either[ApiError, Unit]]
}
