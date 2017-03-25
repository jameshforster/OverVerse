package controllers.tests

import helpers.TestSpec
import models.coordinates.PlanetCoordinateModel
import models.planet.{EnvironmentModel, PlanetModel}
import org.mockito.ArgumentMatchers
import org.scalatestplus.play.OneAppPerSuite
import services.PlanetService
import org.mockito.Mockito._
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest

import scala.concurrent.Future

/**
  * Created by Overlord59 on 25/03/2017.
  */
class TestGeneratorsControllerSpec extends TestSpec with OneAppPerSuite {

  def setupController(planet: Future[PlanetModel]): TestGeneratorsController = {
    val mockService = mock[PlanetService]

    when(mockService.generatePlanet(ArgumentMatchers.any()))
      .thenReturn(planet)

    new TestGeneratorsController(mockService)
  }

  val coordinates = PlanetCoordinateModel(1, 2, 3, 4, 5)
  val planet = PlanetModel(coordinates, 5, EnvironmentModel.barren, Seq())

  "Calling .createPlanet" when {

    "invalid data is passed in the request" should {
      lazy val controller = setupController(Future.failed(new Exception))
      lazy val result = controller.createPlanet()(FakeRequest("POST", "").withJsonBody(Json.toJson("")))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "contain the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "no data is passed in the request" should {
      lazy val controller = setupController(Future.failed(new Exception))
      lazy val result = controller.createPlanet()(FakeRequest("POST", ""))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "contain the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "an unexpected error occurs" should {
      lazy val controller = setupController(Future.failed(new Exception("error message")))
      lazy val result = controller.createPlanet()(FakeRequest("POST", "").withJsonBody(Json.toJson(coordinates)))

      "return a status of 500" in {
        statusOf(result) shouldBe 500
      }

      "contain the correct error message" in {
        bodyOf(result) shouldBe "\"Unexpected error occurred: error message\""
      }
    }

    "a planet is successfully generated" should {
      lazy val controller = setupController(Future.successful(planet))
      lazy val result = controller.createPlanet()(FakeRequest("POST", "").withJsonBody(Json.toJson(coordinates)))

      "return a status of 200" in {
        statusOf(result) shouldBe 200
      }

      "contain the correct error message" in {
        bodyOf(result) shouldBe Json.toJson(planet).toString()
      }
    }
  }
}
