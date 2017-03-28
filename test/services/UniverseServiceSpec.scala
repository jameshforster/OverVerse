package services

import helpers.TestSpec
import models.coordinates.{SectorCoordinateModel, SystemCoordinateModel}
import models.system.{CategoryModel, StarModel, SystemModel}
import org.mockito.ArgumentMatchers
import org.scalatestplus.play.OneAppPerSuite
import org.mockito.Mockito._

import scala.concurrent.Future

/**
  * Created by Overlord59 on 28/03/2017.
  */
class UniverseServiceSpec extends TestSpec with OneAppPerSuite {

  val diceService: DiceService = app.injector.instanceOf[DiceService]
  val system: SystemModel = SystemModel(SystemCoordinateModel(1, 2, 3, 4), StarModel(1, 1, CategoryModel.redDwarf), Seq(), Seq())

  def setupService(system: Future[SystemModel], diceService: DiceService): UniverseService = {
    val mockSystemService = mock[SystemService]

    when(mockSystemService.generateSystem(ArgumentMatchers.any()))
      .thenReturn(system)

    new UniverseService(diceService, mockSystemService)
  }

  def setupMockedService(result: Int): UniverseService = {
    val mockDiceService = mock[DiceService]

    when(mockDiceService.rollD10())
      .thenReturn(Future.successful(result))

    setupService(Future.successful(system), mockDiceService)
  }

  "Calling .generateSector" when {

    "each coordinate has a system" should {
      lazy val service = setupMockedService(10)
      lazy val result = service.generateSector(SectorCoordinateModel(0, 0))

      "contain 100 star systems" in {
        await(result).systems.length shouldBe 100
      }

      "hold the coordinates {0,0}" in {
        await(result).coordinates shouldBe SectorCoordinateModel(0, 0)
      }
    }

    "no coordinates have a system" should {
      lazy val service = setupMockedService(1)
      lazy val result = service.generateSector(SectorCoordinateModel(1, 1))

      "contain no star systems" in {
        await(result).systems.isEmpty shouldBe true
      }

      "hold the coordinates {1,1}" in {
        await(result).coordinates shouldBe SectorCoordinateModel(1, 1)
      }
    }
  }

  "Calling .generateUniverse" when {

    "given a size of 0" should {
      lazy val service = setupMockedService(1)
      lazy val result = service.generateUniverse(0)

      "contain a single sector" in {
        await(result).sectors.length shouldBe 1
      }
    }

    "given a size of 1" should {
      lazy val service = setupMockedService(1)
      lazy val result = service.generateUniverse(1)

      "contain four sectors" in {
        await(result).sectors.length shouldBe 4
      }
    }
  }
}
