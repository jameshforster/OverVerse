package models.universe

import play.api.libs.json.{Json, OFormat}

/**
  * Created by Overlord59 on 30/03/2017.
  */
case class NewUniverseModel(universeName: String, size: Int)

object NewUniverseModel {
  implicit val jsonFormat: OFormat[NewUniverseModel] = Json.format[NewUniverseModel]
}
