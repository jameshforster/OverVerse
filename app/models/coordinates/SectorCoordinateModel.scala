package models.coordinates

import play.api.libs.json.{Json, OFormat}

/**
  * Created by james-forster on 21/03/17.
  */
case class SectorCoordinateModel(X: Int, Y: Int) {
  override val toString: String = s"{$X.$Y}"
}

object SectorCoordinateModel {
  implicit val asJson: OFormat[SectorCoordinateModel] = Json.format[SectorCoordinateModel]
}
