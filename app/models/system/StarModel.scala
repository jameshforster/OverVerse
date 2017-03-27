package models.system

import play.api.libs.json.{Json, OFormat}

/**
  * Created by Overlord59 on 25/03/2017.
  */
case class StarModel(size: Int, age: Int, category: CategoryModel)

object StarModel {
  implicit val jsonFormat: OFormat[StarModel] = Json.format[StarModel]
}
