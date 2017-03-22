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

    when(mockDiceService.rollDX(ArgumentMatchers.any()))
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
}
