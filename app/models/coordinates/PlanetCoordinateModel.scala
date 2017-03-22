package models.coordinates

import play.api.libs.json.{Json, OFormat}

/**
  * Created by james-forster on 14/03/17.
  */
case class PlanetCoordinateModel(systemCoordinateModel: SystemCoordinateModel, z: Int) {
  override val toString: String = s"${systemCoordinateModel.toString}{$z}"
}

object PlanetCoordinateModel {
  implicit val jsonFormat: OFormat[PlanetCoordinateModel] = Json.format[PlanetCoordinateModel]

  def apply(X: Int, Y: Int, x: Int, y: Int, z: Int): PlanetCoordinateModel = {
    PlanetCoordinateModel(SystemCoordinateModel(SectorCoordinateModel(X, Y), x, y), z)
  }
}
