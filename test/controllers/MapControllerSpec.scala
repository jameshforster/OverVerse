package controllers

import helpers.TestSpec
import models.coordinates.SectorCoordinateModel
import models.universe.{SectorMapModel, SectorModel, UniverseModel}
import org.mockito.ArgumentMatchers
import services.MapService
import org.mockito.Mockito._
import play.api.libs.json.Json
import play.api.test.FakeRequest
import services.auth.AuthorisationService

import scala.concurrent.Future

/**
  * Created by Overlord59 on 03/04/2017.
  */
class MapControllerSpec extends TestSpec {

  def setupController(universe: Future[UniverseModel] = Future.successful(validUniverse),
                      sector: Future[Option[SectorModel]] = Future.successful(Some(validSector))): MapController = {
    val mockService = mock[MapService]

    when(mockService.getUniverse(ArgumentMatchers.any()))
      .thenReturn(universe)

    when(mockService.getSector(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(sector)

    new MapController(mockService, mock[AuthorisationService])
  }

  val validUniverse = UniverseModel(Seq(SectorModel(SectorCoordinateModel(0, 0), Seq()), SectorModel(SectorCoordinateModel(0, 1), Seq())))
  val validSector = SectorModel(SectorCoordinateModel(0, 0), Seq())

  "Calling .universeMap" when {

    "an invalid json body is used" should {
      lazy val controller = setupController(Future.successful(validUniverse))
      lazy val result = controller.universeMap(FakeRequest("", "POST").withJsonBody(Json.toJson(validUniverse)))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "return an exception message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "an empty json body is used" should {
      lazy val controller = setupController(Future.successful(validUniverse))
      lazy val result = controller.universeMap(FakeRequest("", "POST"))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "return an exception message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "an exception occurs" should {
      lazy val controller = setupController(Future.failed(new Exception("error message")))
      lazy val result = controller.universeMap(FakeRequest("", "POST").withJsonBody(Json.toJson("testName")))

      "return a status of 500" in {
        statusOf(result) shouldBe 500
      }

      "return an exception message" in {
        bodyOf(result) shouldBe "\"Unexpected error occurred: error message\""
      }
    }

    "an empty UniverseModel is returned" should {
      lazy val controller = setupController(Future.successful(UniverseModel(Seq())))
      lazy val result = controller.universeMap(FakeRequest("", "POST").withJsonBody(Json.toJson("testName")))

      "return a status of 404" in {
        statusOf(result) shouldBe 404
      }

      "return an exception message" in {
        bodyOf(result) shouldBe "\"Resource: testName does not exist.\""
      }
    }

    "a filled UniverseModel is returned" should {
      lazy val controller = setupController(Future.successful(validUniverse))
      lazy val result = controller.universeMap(FakeRequest("", "POST").withJsonBody(Json.toJson("testName")))

      "return a status of 200" in {
        statusOf(result) shouldBe 200
      }

      "contain the universe in the body" in {
        bodyOf(result) shouldBe Json.toJson(validUniverse).toString()
      }
    }
  }

  "Calling .sectorMap" when {

    "an invalid json body is used" should {
      lazy val controller = setupController(sector = Future.successful(Some(validSector)))
      lazy val result = controller.sectorMap(FakeRequest("", "POST").withJsonBody(Json.toJson(validUniverse)))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "return an exception message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "an empty json body is used" should {
      lazy val controller = setupController(sector = Future.successful(Some(validSector)))
      lazy val result = controller.sectorMap(FakeRequest("", "POST"))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "return an exception message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "an exception occurs" should {
      lazy val controller = setupController(sector = Future.failed(new Exception("error message")))
      lazy val result = controller.sectorMap(FakeRequest("", "POST").withJsonBody(Json.toJson(SectorMapModel("testName", SectorCoordinateModel(0, 0)))))

      "return a status of 500" in {
        statusOf(result) shouldBe 500
      }

      "return an exception message" in {
        bodyOf(result) shouldBe "\"Unexpected error occurred: error message\""
      }
    }

    "a None is returned" should {
      lazy val controller = setupController(sector = Future.successful(None))
      lazy val result = controller.sectorMap(FakeRequest("", "POST").withJsonBody(Json.toJson(SectorMapModel("testName", SectorCoordinateModel(0, 0)))))

      "return a status of 404" in {
        statusOf(result) shouldBe 404
      }

      "return an exception message" in {
        bodyOf(result) shouldBe "\"Resource: {0.0} does not exist.\""
      }
    }

    "a sector is returned" should {
      lazy val controller = setupController(sector = Future.successful(Some(validSector)))
      lazy val result = controller.sectorMap(FakeRequest("", "POST").withJsonBody(Json.toJson(SectorMapModel("testName", SectorCoordinateModel(0, 0)))))

      "return a status of 200" in {
        statusOf(result) shouldBe 200
      }

      "contain the sector in the body" in {
        bodyOf(result) shouldBe Json.toJson(validSector).toString()
      }
    }
  }
}
