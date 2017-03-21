package models.system

import models.Entity

/**
  * Created by james-forster on 21/03/17.
  */
case class SystemModel(planets: Seq[SystemPlanetModel], entities: Seq[Entity])
