package controllers.admin

import helpers.TestSpec
import models.exceptions.InsufficientPermissionException
import models.universe.{NewUniverseModel, UniverseModel}
import org.mockito.ArgumentMatchers
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import services.{AdminService, UniverseService}
import org.mockito.Mockito._
import services.auth.AuthorisationService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by james-forster on 03/04/17.
  */
class CreationControllerSpec extends TestSpec with OneAppPerSuite {

  def setupController(universeModel: Future[UniverseModel], saveResponse: Future[Unit],
                      authResponse: Future[Unit] = Future.successful{}): CreationController = {
    val mockUniverseService = mock[UniverseService]
    val mockAdminService = mock[AdminService]
    val mockAuthorisationService = mock[AuthorisationService]

    when(mockUniverseService.generateUniverse(ArgumentMatchers.any()))
      .thenReturn(universeModel)

    when(mockAdminService.storeUniverse(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(saveResponse)

    when(mockAuthorisationService.validateAdmin(ArgumentMatchers.any(), ArgumentMatchers.any()))
    .thenReturn(authResponse)

    new CreationController(mockUniverseService, mockAdminService, mockAuthorisationService)
  }

  val universe = UniverseModel(Seq())
  val requestBody: JsValue = Json.toJson(NewUniverseModel("name", 0))

  "Calling .newUniverse" when {

    "the user is missing authentication credentials" should {
      lazy val controller = setupController(Future.successful(universe), saveResponse = Future.successful{})
      lazy val result = controller.newUniverse(FakeRequest("", "POST")
        .withJsonBody(Json.toJson("")))

      "return a status of 401" in {
        statusOf(result) shouldBe 401
      }

      "contain the correct error message" in {
        bodyOf(result) shouldBe "\"User is not logged in.\""
      }
    }

    "the user is not of a sufficient level to access the resource" should {
      lazy val controller = setupController(Future.successful(universe), saveResponse = Future.successful{},
        authResponse = Future {throw new InsufficientPermissionException(1, 10)})
      lazy val result = controller.newUniverse(FakeRequest("", "POST")
        .withJsonBody(Json.toJson(""))
        .withHeaders(("username", "name"), ("token", "fakeToken")))

      "return a status of 403" in {
        statusOf(result) shouldBe 403
      }

      "contain the correct error message" in {
        bodyOf(result) shouldBe "\"A user with auth level 1 requires a level of 10 to use this resource.\""
      }
    }

    "the authorisation check returns a failure" should {
      lazy val controller = setupController(Future.successful(universe), saveResponse = Future.successful{},
        authResponse = Future {throw new Exception})
      lazy val result = controller.newUniverse(FakeRequest("", "POST")
        .withJsonBody(Json.toJson(""))
        .withHeaders(("username", "name"), ("token", "fakeToken")))

      "return a status of 401" in {
        statusOf(result) shouldBe 401
      }

      "contain the correct error message" in {
        bodyOf(result) shouldBe "\"User is not logged in.\""
      }
    }

    "invalid data is passed in the request body" should {
      lazy val controller = setupController(Future.successful(universe), saveResponse = Future.successful{})
      lazy val result = controller.newUniverse(FakeRequest("", "POST")
        .withJsonBody(Json.toJson(""))
        .withHeaders(("username", "name"), ("token", "fakeToken")))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "contain the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "no data is passed in the request body" should {
      lazy val controller = setupController(Future.successful(universe), saveResponse = Future.successful{})
      lazy val result = controller.newUniverse(FakeRequest("", "POST")
        .withHeaders(("username", "name"), ("token", "fakeToken")))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "contain the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "an unexpected error occurs during universe creation" should {
      lazy val controller = setupController(Future.failed(new Exception("error message")), saveResponse = Future.successful{})
      lazy val result = controller.newUniverse(FakeRequest("", "POST")
        .withJsonBody(requestBody)
        .withHeaders(("username", "name"), ("token", "fakeToken")))

      "return a status of 500" in {
        statusOf(result) shouldBe 500
      }

      "contain the correct error message" in {
        bodyOf(result) shouldBe "\"Unexpected error occurred: error message\""
      }
    }

    "an unexpected error occurs during saving" should {
      lazy val controller = setupController(Future.successful(universe), saveResponse = Future.failed(new Exception("error message")))
      lazy val result = controller.newUniverse(FakeRequest("", "POST")
        .withJsonBody(requestBody)
        .withHeaders(("username", "name"), ("token", "fakeToken")))

      "return a status of 500" in {
        statusOf(result) shouldBe 500
      }

      "contain the correct error message" in {
        bodyOf(result) shouldBe "\"Unexpected error occurred: error message\""
      }
    }

    "a universe is successfully generated and stored" should {
      lazy val controller = setupController(Future.successful(universe), saveResponse = Future.successful{})
      lazy val result = controller.newUniverse(FakeRequest("", "POST")
        .withJsonBody(requestBody)
        .withHeaders(("username", "name"), ("token", "fakeToken")))


      "return a status of 204" in {
        statusOf(result) shouldBe 204
      }
    }
  }
}
