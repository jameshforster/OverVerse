package services

import com.google.inject.{Inject, Singleton}
import models.coordinates.{SectorCoordinateModel, SystemCoordinateModel}
import models.system.SystemModel
import models.universe.{SectorModel, UniverseModel}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Overlord59 on 27/03/2017.
  */
@Singleton
class UniverseService @Inject()(diceService: DiceService, systemService: SystemService) {

  def generateUniverse(size: Int): Future[UniverseModel] = {

    def createSectorRow(x: Int, y: Int = 0): Future[Seq[SectorModel]] = {
      if (y < size) {
        for {
          sector <- generateSector(SectorCoordinateModel(x, y)).map{sector => Seq(sector)}
          universe <- createSectorRow(x, y + 1)
        } yield sector ++ universe
      } else {
        generateSector(SectorCoordinateModel(x, y)).map{sector => Seq(sector)}
      }
    }

    def createSectorGrid(x: Int = 0): Future[Seq[SectorModel]] = {
      if (x < size) {
        for {
          row <- createSectorRow(x)
          grid <- createSectorGrid(x + 1)
        } yield row ++ grid
      } else createSectorRow(x)
    }

    createSectorGrid().map{sectors => UniverseModel(sectors)}
  }

  def generateSector(sectorCoordinateModel: SectorCoordinateModel): Future[SectorModel] = {

    def generateSystem(systemCoordinateModel: SystemCoordinateModel): Future[Seq[SystemModel]] = {
      diceService.rollD10().flatMap { result =>
        if (result == 10) systemService.generateSystem(systemCoordinateModel).map{system => Seq(system)}
        else Future.successful(Seq())
      }
    }

    def createSystemRow(x: Int, y: Int = 0): Future[Seq[SystemModel]] = {
      if (y < 9) {
        for {
          system <- generateSystem(SystemCoordinateModel(sectorCoordinateModel, x, y))
          sector <- createSystemRow(x, y + 1)
        } yield system ++ sector
      } else {
        generateSystem(SystemCoordinateModel(sectorCoordinateModel, x, y))
      }
    }

    def createSystemGrid(x: Int = 0): Future[Seq[SystemModel]] = {
      if (x < 9) {
        for {
          row <- createSystemRow(x)
          grid <- createSystemGrid(x + 1)
        } yield row ++ grid
      } else createSystemRow(x)
    }

    createSystemGrid().map{systems => SectorModel(sectorCoordinateModel, systems)}
  }
}
