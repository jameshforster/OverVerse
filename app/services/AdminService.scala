package services

import com.google.inject.{Inject, Singleton}
import connectors.MongoConnector
import models.universe.{SectorModel, UniverseModel}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Overlord59 on 30/03/2017.
  */

@Singleton
class AdminService @Inject()(mongoConnector: MongoConnector) {

  def storeUniverse(universeName: String, universeModel: UniverseModel): Future[Unit] = {

    def storeSector(sectorModel: SectorModel): Future[Unit] = {
      mongoConnector.putEntry[SectorModel](universeName, sectorModel)
    }

    universeModel.sectors.foldLeft[Future[Unit]](Future.successful {}) {
      (future, component) =>
        future.flatMap {
          _ => storeSector(component)
        }
    }
  }
}
