package services

import connectors.MongoConnector
import helpers.TestSpec
import models.coordinates.SectorCoordinateModel
import models.universe.{SectorModel, UniverseModel}
import org.mockito.ArgumentMatchers
import org.scalatestplus.play.OneAppPerSuite
import org.mockito.Mockito._

import scala.concurrent.Future

/**
  * Created by Overlord59 on 30/03/2017.
  */
class AdminServiceSpec extends TestSpec with OneAppPerSuite {

  def setupService(inputResponse: Future[Unit]): AdminService = {
    val mockConnector = mock[MongoConnector]

    when(mockConnector.putEntry(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(inputResponse)

    new AdminService(mockConnector)
  }

  "Calling .storeUniverse" when {
    val universeModel = UniverseModel(Seq(SectorModel(SectorCoordinateModel(0, 0), Seq()), SectorModel(SectorCoordinateModel(0, 1), Seq())))

    "an error is returned from the connector" should {
      lazy val service = setupService(Future.failed(new Exception("error message")))
      lazy val result = service.storeUniverse("test", universeModel)

      "return the correct error message" in {
        lazy val exception = intercept[Exception](await(result))

        exception.getMessage shouldBe "error message"
      }
    }

    "a success is returned from the connector" should {
      lazy val service = setupService(Future.successful({}))
      lazy val result = service.storeUniverse("test", universeModel)

      "return a Unit" in {
        await(result).isInstanceOf[Unit] shouldBe true
      }
    }
  }
}
