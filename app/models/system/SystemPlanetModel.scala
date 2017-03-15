package models.system

import models.coordinates.PlanetCoordinateModel
import models.planet.Environment
import models.{Validation, Volatility}

/**
  * Created by james-forster on 14/03/17.
  */
case class SystemPlanetModel(coordinate: PlanetCoordinateModel,
                             size: Int,
                             environment: Environment,
                             volatility: Volatility)(name: String = "Unnamed World") {
  Validation.validateSize(size)
}
