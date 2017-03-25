package services

import com.google.inject.{Inject, Singleton}
import contants.AttributeKeys
import models.Attribute
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
      val volatility = planet.attributes.find(_.key == AttributeKeys.volatility).getOrElse(
        Attribute(AttributeKeys.volatility, 0)
      )
      SystemPlanetModel(planet.coordinate, planet.size, planet.environment, volatility)
    }
  }
}
