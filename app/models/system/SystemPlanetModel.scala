package models.system

import models.coordinates.PlanetCoordinateModel
import models.planet.Environment
import models.{Attribute, Validation}

/**
  * Created by james-forster on 14/03/17.
  */
case class SystemPlanetModel(coordinate: PlanetCoordinateModel,
                             size: Int,
                             environment: Environment,
                             volatility: Attribute)(name: String = "Unnamed World") {
  Validation.validateSize(size)
}
