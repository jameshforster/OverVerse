package services

import connectors.MongoConnector
import helpers.TestSpec
import models.coordinates.SectorCoordinateModel
import models.universe.{SectorModel, UniverseModel}
import org.mockito.ArgumentMatchers
import org.scalatestplus.play.OneAppPerSuite
import org.mockito.Mockito._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by james-forster on 03/04/17.
  */
class MapServiceSpec extends TestSpec with OneAppPerSuite {

  def setupService(sectors: Future[List[SectorModel]]): MapService = {
    val mockConnector = mock[MongoConnector]

    when(mockConnector.getAllEntries[SectorModel](ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(sectors)

    when(mockConnector.getEntry[SectorModel](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(sectors.map {_.headOption})

    new MapService(mockConnector)
  }

  "Using the MapService" when {

    "an error occurs with the connector" should {
      lazy val service = setupService(Future.failed(new Exception("error message")))

      "return an exception with the error message from .getUniverse" in {
        lazy val exception = intercept[Exception] {
          await(service.getUniverse(""))
        }

        exception.getMessage shouldBe "error message"
      }

      "return an exception with the error message from .getSector" in {
        lazy val exception = intercept[Exception] {
          await(service.getSector("", SectorCoordinateModel(0, 0)))
        }

        exception.getMessage shouldBe "error message"
      }
    }

    "an empty sequence of sectors are found" should {
      lazy val service = setupService(Future.successful(List()))

      "return an empty UniverseModel from .getUniverse" in {
        lazy val result = service.getUniverse("")

        await(result) shouldBe UniverseModel(Seq())
      }

      "return a None from .getSector" in {
        lazy val result = service.getSector("", SectorCoordinateModel(0, 0))

        await(result) shouldBe None
      }
    }

    "a sequence of sectors are found" should {
      val sectors = List(SectorModel(SectorCoordinateModel(0, 0), Seq()), SectorModel(SectorCoordinateModel(0, 1), Seq()))
      lazy val service = setupService(Future.successful(sectors))

      "return a UniverseModel from .getUniverse" in {
        lazy val result = service.getUniverse("")

        await(result) shouldBe UniverseModel(sectors)
      }

      "return a SectorModel from .getSector" in {
        lazy val result = service.getSector("", SectorCoordinateModel(0, 0))

        await(result) shouldBe Some(SectorModel(SectorCoordinateModel(0, 0), Seq()))
      }
    }
  }

}
