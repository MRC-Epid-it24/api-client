package uk.ac.ncl.openlab.intake24.api.client.roshttp.admin

import fr.hmil.roshttp.Method
import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.client.roshttp.common.HttpServiceUtils
import uk.ac.ncl.openlab.intake24.api.client.services.admin.FoodsAdminService
import uk.ac.ncl.openlab.intake24.api.data.admin.{FoodRecord, LocalFoodRecordUpdate}
import io.circe.generic.auto._
import uk.ac.ncl.openlab.intake24.api.client.services.AuthRequestHandler

import scala.concurrent.Future

class FoodsAdminImpl(val authRequestHandler: AuthRequestHandler) extends FoodsAdminService with HttpServiceUtils {

  def getFoodRecord(accessToken: String, foodCode: String, locale: String): Future[Either[ApiError, FoodRecord]] =
    authRequestHandler.sendWithAccessToken[FoodRecord](request(Method.GET, s"/admin/foods/$locale/$foodCode"))

  def updateLocalFoodRecord(accessToken: String, foodCode: String, locale: String, update: LocalFoodRecordUpdate): Future[Either[ApiError, Unit]] =
    authRequestHandler.sendWithAccessToken[Unit](requestWithBody(Method.POST, s"/admin/foods/$locale/$foodCode", update))
}
