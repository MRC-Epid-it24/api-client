package uk.ac.ncl.openlab.intake24.api.client.roshttp.admin

import fr.hmil.roshttp.Method
import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.client.roshttp.common.HttpServiceUtils
import uk.ac.ncl.openlab.intake24.api.client.services.admin.FoodsAdminService
import uk.ac.ncl.openlab.intake24.api.data.admin.{FoodRecord, LocalFoodRecordUpdate}
import io.circe.generic.auto._
import scala.concurrent.Future

class FoodsAdminImpl(val apiBaseUrl: String) extends FoodsAdminService with HttpServiceUtils {

  def getFoodRecord(accessToken: String, foodCode: String, locale: String): Future[Either[ApiError, FoodRecord]] =
    authRequestWithResponse[FoodRecord](Method.GET, s"/admin/foods/$locale/$foodCode", accessToken)

  def updateLocalFoodRecord(accessToken: String, foodCode: String, locale: String, update: LocalFoodRecordUpdate): Future[Either[ApiError, Unit]] =
    authRequestWithBody(Method.POST, s"$apiBaseUrl/admin/foods/$locale/$foodCode", accessToken, update)
}
