package models.universe

import models.coordinates.SectorCoordinateModel
import play.api.libs.json.{Json, OFormat}

/**
  * Created by Overlord59 on 04/04/2017.
  */
case class SectorMapModel(universeName: String, coordinates: SectorCoordinateModel)

object SectorMapModel {
  implicit val jsonFormat: OFormat[SectorMapModel] = Json.format[SectorMapModel]
}
