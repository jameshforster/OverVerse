package models.universe

import models.coordinates.SectorCoordinateModel
import models.system.SystemModel
import play.api.libs.json.{Json, OFormat}

/**
  * Created by james-forster on 21/03/17.
  */
case class SectorModel(coordinates: SectorCoordinateModel, systems: Seq[SystemModel])

object SectorModel {
  implicit val jsonFormat: OFormat[SectorModel] = Json.format[SectorModel]
}
