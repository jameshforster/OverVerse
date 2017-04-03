package services

import com.google.inject.{Inject, Singleton}
import connectors.MongoConnector
import models.coordinates.SectorCoordinateModel
import models.universe.{SectorModel, UniverseModel}
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by james-forster on 03/04/17.
  */

@Singleton
class MapService @Inject()(mongoConnector: MongoConnector) {

  def getUniverse(universeName: String): Future[UniverseModel] = {
    mongoConnector.getAllEntries[SectorModel](universeName).map { sectors =>
      UniverseModel(sectors)
    }
  }

  def getSector(universeName: String, sectorCoordinateModel: SectorCoordinateModel): Future[Option[SectorModel]] = {
    mongoConnector.getEntry[SectorModel, SectorCoordinateModel](universeName, "coordinates", Json.toJson(sectorCoordinateModel))
  }
}
