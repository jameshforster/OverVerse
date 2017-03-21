package models.coordinates

import play.api.libs.json.{Json, OFormat}

/**
  * Created by james-forster on 14/03/17.
  */
case class PlanetCoordinateModel(systemCoordinateModel: SystemCoordinateModel, z: Int) {
  override val toString: String = s"${systemCoordinateModel.toString}{$z}"
}

object PlanetCoordinateModel {
  implicit val asJson: OFormat[PlanetCoordinateModel] = Json.format[PlanetCoordinateModel]
}
