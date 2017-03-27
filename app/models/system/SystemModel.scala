package models.system

import models.Entity
import models.coordinates.SystemCoordinateModel
import play.api.libs.json.{Json, OFormat}

/**
  * Created by james-forster on 21/03/17.
  */
case class SystemModel(coordinates: SystemCoordinateModel, star:StarModel, planets: Seq[SystemPlanetModel], entities: Seq[Entity])

object SystemModel {
  implicit val jsonFormat: OFormat[SystemModel] = Json.format[SystemModel]
}
