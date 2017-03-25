package services

import com.google.inject.{Inject, Singleton}
import models.coordinates.PlanetCoordinateModel
import models.system.SystemPlanetModel

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Overlord59 on 25/03/2017.
  */
@Singleton
class SystemService @Inject()(planetService: PlanetService, diceService: DiceService) {

  def createPlanet(coordinates: PlanetCoordinateModel): Future[SystemPlanetModel] = {
    planetService.generatePlanet(coordinates).map { planet =>
      SystemPlanetModel(planet.coordinate, planet.size, planet.environment)
    }
  }
}
