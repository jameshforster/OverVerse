package models.coordinates

import play.api.libs.json.{Json, OFormat}

/**
  * Created by james-forster on 14/03/17.
  */
case class SystemCoordinateModel(X: Int, Y: Int, x: Int, y: Int) {
  override val toString: String = s"$X.$Y,$x.$y"
}

object SystemCoordinateModel {
  implicit val asJson: OFormat[SystemCoordinateModel] = Json.format[SystemCoordinateModel]
}
