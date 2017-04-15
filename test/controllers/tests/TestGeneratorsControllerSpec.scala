package controllers.tests

import helpers.TestSpec
import models.coordinates.{PlanetCoordinateModel, SystemCoordinateModel}
import models.planet.{EnvironmentModel, PlanetModel}
import models.system.{CategoryModel, StarModel, SystemModel}
import models.universe.UniverseModel
import org.mockito.ArgumentMatchers
import org.scalatestplus.play.OneAppPerSuite
import services.{PlanetService, SystemService, UniverseService}
import org.mockito.Mockito._
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import services.auth.AuthorisationService

import scala.concurrent.Future

/**
  * Created by Overlord59 on 25/03/2017.
  */
class TestGeneratorsControllerSpec extends TestSpec with OneAppPerSuite {

  def setupController(planet: Future[PlanetModel], system: Future[SystemModel], universe: Future[UniverseModel]): TestGeneratorsController = {
    val mockPlanetService = mock[PlanetService]
    val mockSystemService = mock[SystemService]
    val mockUniverseService = mock[UniverseService]

    when(mockPlanetService.generatePlanet(ArgumentMatchers.any()))
      .thenReturn(planet)

    when(mockSystemService.generateSystem(ArgumentMatchers.any()))
      .thenReturn(system)

    when(mockUniverseService.generateUniverse(ArgumentMatchers.any()))
      .thenReturn(universe)

    new TestGeneratorsController(mockPlanetService, mockSystemService, mockUniverseService, mock[AuthorisationService])
  }

  val planetCoordinates = PlanetCoordinateModel(1, 2, 3, 4, 5)
  val systemCoordinates = SystemCoordinateModel(1, 2, 3, 4)
  val planet = PlanetModel(planetCoordinates, 5, EnvironmentModel.barren, Seq())
  val system = SystemModel(systemCoordinates, StarModel(1, 1, CategoryModel.redDwarf), Seq(), Seq())
  val universe = UniverseModel(Seq())

  "Calling .createPlanet" when {

    "invalid data is passed in the request" should {
      lazy val controller = setupController(Future.failed(new Exception), Future.successful(system), Future.successful(universe))
      lazy val result = controller.createPlanet()(FakeRequest("POST", "").withJsonBody(Json.toJson("")))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "contain the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "no data is passed in the request" should {
      lazy val controller = setupController(Future.failed(new Exception), Future.successful(system), Future.successful(universe))
      lazy val result = controller.createPlanet()(FakeRequest("POST", ""))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "contain the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "an unexpected error occurs" should {
      lazy val controller = setupController(Future.failed(new Exception("error message")), Future.successful(system), Future.successful(universe))
      lazy val result = controller.createPlanet()(FakeRequest("POST", "").withJsonBody(Json.toJson(planetCoordinates)))

      "return a status of 500" in {
        statusOf(result) shouldBe 500
      }

      "contain the correct error message" in {
        bodyOf(result) shouldBe "\"Unexpected error occurred: error message\""
      }
    }

    "a planet is successfully generated" should {
      lazy val controller = setupController(Future.successful(planet), Future.successful(system), Future.successful(universe))
      lazy val result = controller.createPlanet()(FakeRequest("POST", "").withJsonBody(Json.toJson(planetCoordinates)))

      "return a status of 200" in {
        statusOf(result) shouldBe 200
      }

      "contain the planet in the body" in {
        bodyOf(result) shouldBe Json.toJson(planet).toString()
      }
    }
  }

  "Calling .createSystem" when {

    "invalid data is passed in the request" should {
      lazy val controller = setupController(Future.successful(planet), Future.successful(system), Future.successful(universe))
      lazy val result = controller.createSystem()(FakeRequest("POST", "").withJsonBody(Json.toJson("")))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "contain the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "no data is passed in the request" should {
      lazy val controller = setupController(Future.successful(planet), Future.successful(system), Future.successful(universe))
      lazy val result = controller.createSystem()(FakeRequest("POST", ""))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "contain the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "an unexpected error occurs" should {
      lazy val controller = setupController(Future.successful(planet), Future.failed(new Exception("error message")), Future.successful(universe))
      lazy val result = controller.createSystem()(FakeRequest("POST", "").withJsonBody(Json.toJson(systemCoordinates)))

      "return a status of 500" in {
        statusOf(result) shouldBe 500
      }

      "contain the correct error message" in {
        bodyOf(result) shouldBe "\"Unexpected error occurred: error message\""
      }
    }

    "a system is successfully generated" should {
      lazy val controller = setupController(Future.successful(planet), Future.successful(system), Future.successful(universe))
      lazy val result = controller.createSystem()(FakeRequest("POST", "").withJsonBody(Json.toJson(systemCoordinates)))

      "return a status of 200" in {
        statusOf(result) shouldBe 200
      }

      "contain the system in the body" in {
        bodyOf(result) shouldBe Json.toJson(system).toString()
      }
    }
  }

  "Calling .createUniverse" when {

    "an unexpected error occurs" should {
      lazy val controller = setupController(Future.successful(planet), Future.successful(system), Future.failed(new Exception("error message")))
      lazy val result = controller.createUniverse(0)(FakeRequest("GET", ""))

      "return a status of 500" in {
        statusOf(result) shouldBe 500
      }

      "contain the correct error message" in {
        bodyOf(result) shouldBe "\"Unexpected error occurred: error message\""
      }
    }

    "a universe is successfully generated" should {
      lazy val controller = setupController(Future.successful(planet), Future.successful(system), Future.successful(universe))
      lazy val result = controller.createUniverse(0)(FakeRequest("GET", ""))

      "return a status of 200" in {
        statusOf(result) shouldBe 200
      }

      "contain the universe in the body" in {
        bodyOf(result) shouldBe Json.toJson(universe).toString()
      }
    }
  }
}
