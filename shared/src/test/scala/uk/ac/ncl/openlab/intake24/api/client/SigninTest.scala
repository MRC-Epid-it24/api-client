package uk.ac.ncl.openlab.intake24.api.client

import uk.ac.ncl.openlab.intake24.api.client.roshttp.common.SigninImpl
import uk.ac.ncl.openlab.intake24.api.shared.EmailCredentials
import cats._
import cats.data._
import cats.implicits._

import scala.concurrent.Future

class SigninTest extends ApiTestSuite {

  val service = new SigninImpl(apiBaseUrl)

  test("Sign in with bad e-mail") {
    service.signin(EmailCredentials("hfkdhfkjsdhfkjds", "fhdlskfhkljewhfljw")).map(assertHttpError(_, 401))
  }

  test("Sign in with e-mail using test user id (from environment variables)") {
    service.signin(EmailCredentials(apiUser, apiPassword)).map(assertRequestSuccessful)
  }

  test("Refresh an access token") {
    (for (
      signinResult <- EitherT(service.signin(EmailCredentials(apiUser, apiPassword)));
      _ <- EitherT(service.refresh(signinResult.refreshToken)))
      yield ()).value.map(assertRequestSuccessful)
  }
}
