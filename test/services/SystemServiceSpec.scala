package services

import contants.AttributeKeys
import helpers.TestSpec
import models.Attribute
import models.coordinates.PlanetCoordinateModel
import models.planet.{EnvironmentModel, PlanetModel}
import models.system.SystemPlanetModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatestplus.play.OneAppPerSuite

import scala.concurrent.Future

/**
  * Created by Overlord59 on 25/03/2017.
  */
class SystemServiceSpec extends TestSpec with OneAppPerSuite {

  val diceService: DiceService = app.injector.instanceOf[DiceService]

  val planet = PlanetModel(PlanetCoordinateModel(1, 2, 3, 4, 5), 5, EnvironmentModel.magma, Seq(Attribute(AttributeKeys.volatility, 2)))
  val systemPlanetModel = SystemPlanetModel(PlanetCoordinateModel(1, 2, 3, 4, 5), 5, EnvironmentModel.magma)

  def setupService(planet: Future[PlanetModel], diceService: DiceService): SystemService = {
    val mockPlanetService = mock[PlanetService]

    when(mockPlanetService.generatePlanet(ArgumentMatchers.any()))
      .thenReturn(planet)

    new SystemService(mockPlanetService, diceService)
  }

  "Calling .createPlanet" when {

    "an exception occurs in the planetService" should {
      lazy val service = setupService(Future.failed(new Exception("Error message")), diceService)
      lazy val result = service.createPlanet(mock[PlanetCoordinateModel])

      "maintain the failed future" in {
        val exception = intercept[Exception] {
          await(result)
        }

        exception.getMessage shouldBe "Error message"
      }
    }

    "a valid planet is created" should {
      lazy val service = setupService(Future.successful(planet), diceService)
      lazy val result = service.createPlanet(mock[PlanetCoordinateModel])

      "return an equivalent SystemPlanetModel" in {
        await(result) shouldBe systemPlanetModel
      }
    }
  }
}
