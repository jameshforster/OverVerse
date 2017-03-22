package models.coordinates

import play.api.libs.json.{Json, OFormat}

/**
  * Created by james-forster on 14/03/17.
  */
case class SystemCoordinateModel(sectorCoordinateModel: SectorCoordinateModel, x: Int, y: Int) {
  override val toString: String = s"${sectorCoordinateModel.toString}{$x.$y}"
}

object SystemCoordinateModel {
  implicit val jsonFormat: OFormat[SystemCoordinateModel] = Json.format[SystemCoordinateModel]

  def apply(X: Int, Y: Int, x: Int, y: Int): SystemCoordinateModel = {
    SystemCoordinateModel(SectorCoordinateModel(X, Y), x, y)
  }
}
