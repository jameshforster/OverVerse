package models.universe

import play.api.libs.json.{Json, OFormat}

/**
  * Created by Overlord59 on 21/03/2017.
  */
case class UniverseModel(sectors: Seq[SectorModel])

object UniverseModel {
  implicit val jsonFormat: OFormat[UniverseModel] = Json.format[UniverseModel]
}