package services

import helpers.TestSpec
import models.Attribute
import models.planet.EnvironmentModel
import org.mockito.ArgumentMatchers
import org.scalatestplus.play.OneAppPerSuite
import org.mockito.Mockito._

import scala.concurrent.Future

/**
  * Created by james-forster on 22/03/17.
  */
class PlanetServiceSpec extends TestSpec with OneAppPerSuite {

  lazy val diceService = app.injector.instanceOf[DiceService]

  def setupService(diceService: DiceService): PlanetService = {
    new PlanetService(diceService)
  }

  def setupMockedService(randomResult: Int): PlanetService = {
    val mockDiceService = mock[DiceService]

    when(mockDiceService.rollDX(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(randomResult))

    setupService(mockDiceService)
  }

  "Calling .generateEnvironment" when {

    "only a single valid environment is available" should {
      val attributes: Seq[Attribute] = Seq()

      "return an environment of 'Barren'" in {
        lazy val service = setupService(diceService)
        lazy val result = service.generateEnvironment(attributes)

        await(result) shouldBe EnvironmentModel.barren
      }
    }

    "multiple environments are available" should {
      val attributes: Seq[Attribute] = Seq(Attribute("Atmosphere", 1), Attribute("Fertility", 0))

      "return an environment of 'Barren' on a 0" in {
        lazy val service = setupMockedService(0)
        lazy val result = service.generateEnvironment(attributes)

        await(result) shouldBe EnvironmentModel.barren
      }

      "return an environment of 'Mountainous' on a 1" in {
        lazy val service = setupMockedService(1)
        lazy val result = service.generateEnvironment(attributes)

        await(result) shouldBe EnvironmentModel.mountainous
      }
    }
  }

  "Calling .generateAttribute" should {

    "create an attribute 'testKey' with a value returned by the dice roller" in {
      lazy val service = setupMockedService(3)
      lazy val result = service.generateAttribute("testKey")

      await(result) shouldBe Attribute("testKey", 3)
    }

    "create an attribute 'testKey2' with a value returned by the dice roller" in {
      lazy val service = setupMockedService(0)
      lazy val result = service.generateAttribute("testKey2")

      await(result) shouldBe Attribute("testKey2", 0)
    }

    "return a value for attribute between 0 and 5" in {
      lazy val service = setupService(diceService)
      lazy val attributeValue = await(service.generateAttribute("testKey")).value
      lazy val result = attributeValue <= 5 && attributeValue >= 0

      result shouldBe true
    }

    "not return a value for attribute greater than 5" in {
      lazy val service = setupService(diceService)
      lazy val attributeValue = await(service.generateAttribute("testKey")).value
      lazy val result = attributeValue > 5

      result shouldBe false
    }

    "not return a value for attribute less than 0" in {
      lazy val service = setupService(diceService)
      lazy val attributeValue = await(service.generateAttribute("testKey")).value
      lazy val result = attributeValue < 0

      result shouldBe false
    }
  }

  "Calling .generatePlanetSize" should {
    lazy val service = setupService(diceService)
    lazy val size = await(service.generatePlanetSize())

    "return a value for size between 3 and 10" in {
      lazy val result = size <= 10 && size >= 3

      result shouldBe true
    }

    "not return a value less than 3" in {
      lazy val result = size < 3

      result shouldBe false
    }

    "not return a value greater than 10" in {
      lazy val result = size > 10

      result shouldBe false
    }
  }
}
