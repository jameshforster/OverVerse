package services

import helpers.TestSpec
import models.Attribute
import models.coordinates.PlanetCoordinateModel
import models.planet.{EnvironmentModel, PlanetModel}
import org.mockito.ArgumentMatchers
import org.scalatestplus.play.OneAppPerSuite
import org.mockito.Mockito._

import scala.concurrent.Future

/**
  * Created by james-forster on 22/03/17.
  */
class PlanetServiceSpec extends TestSpec with OneAppPerSuite {

  lazy val diceService: DiceService = app.injector.instanceOf[DiceService]

  def setupService(diceService: DiceService): PlanetService = {
    new PlanetService(diceService)
  }

  def setupMockedService(randomResult: Int): PlanetService = {
    val mockDiceService = mock[DiceService]

    when(mockDiceService.rollDX(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(randomResult))

    setupService(mockDiceService)
  }

  "Calling .generatePlanet" should {
    lazy val coordinates = PlanetCoordinateModel(1, 2, 3, 4, 2)
    lazy val service = setupService(diceService)

    "create a valid planet" in {
      lazy val result = service.generatePlanet(coordinates)

      await(result).isInstanceOf[PlanetModel] shouldBe true
    }
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

  "Calling .extractAttributeValue" should {
    lazy val service = setupService(diceService)

    "return a value when found" in {
      val attributes = Seq(Attribute("test", 3), Attribute("test2", 5))
      lazy val result = service.extractAttributeValue(attributes, "test2")

      result shouldBe 5
    }

    "return a default value of 0 when not found" in {
      val attributes = Seq(Attribute("test", 3), Attribute("test2", 5))
      lazy val result = service.extractAttributeValue(attributes, "test3")

      result shouldBe 0
    }
  }

  "Calling .generatePrimaryAttributes" should {

    "return a valid sequence of attributes" when {

      "the dice roller returns 4" in {
        lazy val service = setupMockedService(4)
        lazy val result = service.generatePrimaryAttributes(PlanetCoordinateModel(1, 2, 3, 4, 5))

        await(result) shouldBe Seq(Attribute("Solar", 0), Attribute("Atmosphere", 4), Attribute("Metal", 4), Attribute("Fuel", 4), Attribute("Nuclear", 4), Attribute("Volatility", 4))
      }

      "the dice roller returns 2" in {
        lazy val service = setupMockedService(2)
        lazy val result = service.generatePrimaryAttributes(PlanetCoordinateModel(1, 2, 3, 4, 0))

        await(result) shouldBe Seq(Attribute("Solar", 5), Attribute("Atmosphere", 2), Attribute("Metal", 2), Attribute("Fuel", 2), Attribute("Nuclear", 2), Attribute("Volatility", 2))
      }
    }
  }

  "Calling .generateSecondaryAttributes" should {

    "return a list with the original array" in {
      val seq = Seq(Attribute("Solar", 0), Attribute("Atmosphere", 4))
      lazy val service = setupService(diceService)
      lazy val result = service.generateSecondaryAttributes(seq)

      await(result).containsSlice(seq) shouldBe true
    }

    "return a list containing the Temperature attribute" which {

      "has a value between 0 and 5 when result cannot be greater than 5" in {
        val seq = Seq(Attribute("Solar", 0), Attribute("Atmosphere", 0))
        lazy val service = setupService(diceService)
        lazy val attributes = service.generateSecondaryAttributes(seq)
        lazy val result = service.extractAttributeValue(await(attributes), "Temperature")

        result <= 5 && result >= 0 shouldBe true
      }

      "has a value of 5 when result cannot is greater than 5" in {
        val seq = Seq(Attribute("Solar", 0), Attribute("Atmosphere", 0))
        lazy val service = setupMockedService(6)
        lazy val attributes = service.generateSecondaryAttributes(seq)
        lazy val result = service.extractAttributeValue(await(attributes), "Temperature")

        result == 5 shouldBe true
      }
    }

    "return a list containing the Wind attribute" which {

      "has a value between 2 and 4 with an atmosphere of 2" in {
        val seq = Seq(Attribute("Solar", 0), Attribute("Atmosphere", 2))
        lazy val service = setupService(diceService)
        lazy val attributes = service.generateSecondaryAttributes(seq)
        lazy val result = service.extractAttributeValue(await(attributes), "Wind")

        result <= 4 && result >= 2 shouldBe true
      }

      "has a value between 2 and 4 with an atmosphere of 3" in {
        val seq = Seq(Attribute("Solar", 0), Attribute("Atmosphere", 3))
        lazy val service = setupService(diceService)
        lazy val attributes = service.generateSecondaryAttributes(seq)
        lazy val result = service.extractAttributeValue(await(attributes), "Wind")

        result <= 4 && result >= 2 shouldBe true
      }

      "has a value between 2 and 4 with an atmosphere of 4" in {
        val seq = Seq(Attribute("Solar", 0), Attribute("Atmosphere", 4))
        lazy val service = setupService(diceService)
        lazy val attributes = service.generateSecondaryAttributes(seq)
        lazy val result = service.extractAttributeValue(await(attributes), "Wind")

        result <= 4 && result >= 2 shouldBe true
      }

      "have a value equal to the atmosphere with a value less than 2" in {
        val seq = Seq(Attribute("Solar", 0), Attribute("Atmosphere", 1))
        lazy val service = setupService(diceService)
        lazy val result = service.generateSecondaryAttributes(seq)

        await(result).contains(Attribute("Wind", 1)) shouldBe true
      }

      "have a value equal to the atmosphere with a value greater than 4" in {
        val seq = Seq(Attribute("Solar", 0), Attribute("Atmosphere", 5))
        lazy val service = setupService(diceService)
        lazy val result = service.generateSecondaryAttributes(seq)

        await(result).contains(Attribute("Wind", 5)) shouldBe true
      }
    }
  }

  "Calling .generateTertiaryAttributes" should {

    "return a list with the original array" in {
      val seq = Seq(Attribute("Temperature", 2), Attribute("Volatility", 2), Attribute("Atmosphere", 2))
      lazy val service = setupService(diceService)
      lazy val result = service.generateTertiaryAttributes(seq)

      await(result).containsSlice(seq) shouldBe true
    }

    "return a list containing the Water Attribute" which {

      "has a value between 0 and 1 with a temperature of 4" in {
        val seq = Seq(Attribute("Temperature", 4), Attribute("Volatility", 2), Attribute("Atmosphere", 2))
        lazy val service = setupService(diceService)
        lazy val attributes = service.generateTertiaryAttributes(seq)
        lazy val result = service.extractAttributeValue(await(attributes), "Water")

        result <= 1 && result >= 0 shouldBe true
      }

      "has a value of 0 with a temperature of 5" in {
        val seq = Seq(Attribute("Temperature", 5), Attribute("Volatility", 2), Attribute("Atmosphere", 2))
        lazy val service = setupService(diceService)
        lazy val result = service.generateTertiaryAttributes(seq)

        await(result).contains(Attribute("Water", 0))
      }

      "has a value between 0 and 5 with a temperature less than 4" in {
        val seq = Seq(Attribute("Temperature", 3), Attribute("Volatility", 2), Attribute("Atmosphere", 2))
        lazy val service = setupService(diceService)
        lazy val attributes = service.generateTertiaryAttributes(seq)
        lazy val result = service.extractAttributeValue(await(attributes), "Water")

        result <= 5 && result >= 0 shouldBe true
      }
    }

    "return a list containing the Fertility Attribute" which {

      "has a value between 0 and 5 with valid atmosphere and temperature of 2" in {
        val seq = Seq(Attribute("Temperature", 2), Attribute("Volatility", 2), Attribute("Atmosphere", 2))
        lazy val service = setupService(diceService)
        lazy val attributes = service.generateTertiaryAttributes(seq)
        lazy val result = service.extractAttributeValue(await(attributes), "Fertility")

        result <= 5 && result >= 0 shouldBe true
      }

      "has a value between 0 and 5 with valid atmosphere and temperature of 3" in {
        val seq = Seq(Attribute("Temperature", 3), Attribute("Volatility", 2), Attribute("Atmosphere", 3))
        lazy val service = setupService(diceService)
        lazy val attributes = service.generateTertiaryAttributes(seq)
        lazy val result = service.extractAttributeValue(await(attributes), "Fertility")

        result <= 5 && result >= 0 shouldBe true
      }

      "has a value between 0 and 5 with invalid atmosphere and temperature but volatility greater than 3" in {
        val seq = Seq(Attribute("Temperature", 1), Attribute("Volatility", 4), Attribute("Atmosphere", 1))
        lazy val service = setupService(diceService)
        lazy val attributes = service.generateTertiaryAttributes(seq)
        lazy val result = service.extractAttributeValue(await(attributes), "Fertility")

        result <= 5 && result >= 0 shouldBe true
      }

      "has a value of 0 with invalid atmosphere, temperature and volatility" in {
        val seq = Seq(Attribute("Temperature", 1), Attribute("Volatility", 3), Attribute("Atmosphere", 1))
        lazy val service = setupService(diceService)
        lazy val attributes = service.generateTertiaryAttributes(seq)
        lazy val result = service.extractAttributeValue(await(attributes), "Fertility")

        result == 0 shouldBe true
      }
    }
  }
}
