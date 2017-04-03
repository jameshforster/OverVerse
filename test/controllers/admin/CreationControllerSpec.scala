package controllers.admin

import helpers.TestSpec
import models.universe.{NewUniverseModel, UniverseModel}
import org.mockito.ArgumentMatchers
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.Json
import play.api.test.FakeRequest
import services.{AdminService, UniverseService}
import org.mockito.Mockito._

import scala.concurrent.Future

/**
  * Created by james-forster on 03/04/17.
  */
class CreationControllerSpec extends TestSpec with OneAppPerSuite {

  def setupController(universeModel: Future[UniverseModel], saveResponse: Future[Unit]) = {
    val mockUniverseService = mock[UniverseService]
    val mockAdminService = mock[AdminService]

    when(mockUniverseService.generateUniverse(ArgumentMatchers.any()))
      .thenReturn(universeModel)

    when(mockAdminService.storeUniverse(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(saveResponse)

    new CreationController(mockUniverseService, mockAdminService)
  }

  val universe = UniverseModel(Seq())
  val requestBody = Json.toJson(NewUniverseModel("name", 0))

  "Calling .newUniverse" when {

    "invalid data is passed in the request body" should {
      lazy val controller = setupController(Future.successful(universe), saveResponse = Future.successful{})
      lazy val result = controller.newUniverse(FakeRequest("", "POST").withJsonBody(Json.toJson("")))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "contain the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "no data is passed in the request body" should {
      lazy val controller = setupController(Future.successful(universe), saveResponse = Future.successful{})
      lazy val result = controller.newUniverse(FakeRequest("", "POST"))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "contain the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "an unexpected error occurs during universe creation" should {
      lazy val controller = setupController(Future.failed(new Exception("error message")), saveResponse = Future.successful{})
      lazy val result = controller.newUniverse(FakeRequest("", "POST").withJsonBody(requestBody))

      "return a status of 500" in {
        statusOf(result) shouldBe 500
      }

      "contain the correct error message" in {
        bodyOf(result) shouldBe "\"Unexpected error occurred: error message\""
      }
    }

    "an unexpected error occurs during saving" should {
      lazy val controller = setupController(Future.successful(universe), saveResponse = Future.failed(new Exception("error message")))
      lazy val result = controller.newUniverse(FakeRequest("", "POST").withJsonBody(requestBody))

      "return a status of 500" in {
        statusOf(result) shouldBe 500
      }

      "contain the correct error message" in {
        bodyOf(result) shouldBe "\"Unexpected error occurred: error message\""
      }
    }

    "a universe is successfully generated and stored" should {
      lazy val controller = setupController(Future.successful(universe), saveResponse = Future.successful{})
      lazy val result = controller.newUniverse(FakeRequest("", "POST").withJsonBody(requestBody))


      "return a status of 204" in {
        statusOf(result) shouldBe 204
      }
    }
  }
}
