package models.planet

import models.Attribute
import models.coordinates.PlanetCoordinateModel
import play.api.libs.json.{Json, OFormat}

/**
  * Created by Overlord59 on 22/03/2017.
  */
case class PlanetModel(coordinate: PlanetCoordinateModel,
                       size: Int,
                       environment: Environment,
                       attributes: Seq[Attribute],
                       name: String = "Unnamed World")

object PlanetModel {
  implicit val jsonFormat: OFormat[PlanetModel] = Json.format[PlanetModel]
}