package services

import contants.AttributeKeys
import helpers.TestSpec
import models.Attribute
import models.coordinates.PlanetCoordinateModel
import models.planet.{EnvironmentModel, PlanetModel}
import models.system.{CategoryModel, StarModel, SystemPlanetModel}
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

  def setupMockedService(randomResult: Int, additionalResult: Int = 0): SystemService = {
    val mockDiceService = mock[DiceService]

    when(mockDiceService.rollDX(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(additionalResult))

    when(mockDiceService.rollDX(ArgumentMatchers.any(), ArgumentMatchers.eq(1)))
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
        lazy val service = setupMockedService(1, 1)
        lazy val result = service.determineCategory(5, 1)

        await(result) shouldBe CategoryModel.blueStar
      }
    }
  }

  "Calling .determineAge" should {
    lazy val service = setupService(Future.successful(planet), diceService)

    "return a value between 1 and 5" in {
      lazy val age = await(service.determineAge())
      lazy val result = age >= 1 && age <= 5

      result shouldBe true
    }

    "not return a value less than 1" in {
      lazy val result = service.determineAge()

      await(result) < 1 shouldBe false
    }

    "not return a value greater than 5" in {
      lazy val result = service.determineAge()

      await(result) > 5 shouldBe false
    }
  }

  "Calling .determineSize" should {
    lazy val service = setupService(Future.successful(planet), diceService)

    "return a value between 1 and 6" in {
      lazy val size = await(service.determineSize())
      lazy val result = size >= 1 && size <= 6

      result shouldBe true
    }

    "not return a value less than 1" in {
      lazy val result = service.determineSize()

      await(result) < 1 shouldBe false
    }

    "not return a value greater than 6" in {
      lazy val result = service.determineSize()

      await(result) > 6 shouldBe false
    }
  }

  "Calling .createStar" should {

    "return a star with a valid size and age" in {
      lazy val service = setupMockedService(1)
      lazy val result = service.createStar()

      await(result) shouldBe StarModel(1, 1, CategoryModel.redDwarf)
    }

    "return an exception with invalid size or age" in {
      lazy val service = setupMockedService(8)
      lazy val exception = intercept[Exception] {
        await(service.createStar())
      }

      exception.getMessage shouldBe "0"
    }
  }
}
