package models.system

import models.Entity
import models.coordinates.SystemCoordinateModel

/**
  * Created by james-forster on 21/03/17.
  */
case class SystemModel(coordinates: SystemCoordinateModel, planets: Seq[SystemPlanetModel], entities: Seq[Entity])
