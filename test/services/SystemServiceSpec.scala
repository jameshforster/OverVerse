package services

import contants.AttributeKeys
import helpers.TestSpec
import models.Attribute
import models.coordinates.PlanetCoordinateModel
import models.planet.{EnvironmentModel, PlanetModel}
import models.system.{CategoryModel, SystemPlanetModel}
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

  def setupMockedService(randomResult: Int): SystemService = {
    val mockDiceService = mock[DiceService]

    when(mockDiceService.rollDX(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(randomResult))

    setupService(Future.successful(planet), mockDiceService)
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

  "Calling .determineCategory" when {

    "only a single valid category is available" should {
      lazy val service = setupService(Future.successful(planet), diceService)
      lazy val result = service.determineCategory(1, 1)

      "return an category of 'Red Dwarf'" in {
        await(result) shouldBe CategoryModel.redDwarf
      }
    }

    "multiple categories are available" should {

      "return an category of 'White Star' on a 0" in {
        lazy val service = setupMockedService(0)
        lazy val result = service.determineCategory(5, 1)

        await(result) shouldBe CategoryModel.whiteStar
      }

      "return an category of 'Blue Star' on a 1" in {
        lazy val service = setupMockedService(1)
        lazy val result = service.determineCategory(5, 1)

        await(result) shouldBe CategoryModel.blueStar
      }
    }
  }
}
