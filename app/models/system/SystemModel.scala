package models.system

import models.Entity
import models.coordinates.SystemCoordinateModel
import play.api.libs.json.{Json, OFormat}

/**
  * Created by james-forster on 21/03/17.
  */
case class SystemModel(coordinates: SystemCoordinateModel, planets: Seq[SystemPlanetModel], entities: Seq[Entity])

object SystemModel {
  implicit val asJson: OFormat[SystemModel] = Json.format[SystemModel]
}
