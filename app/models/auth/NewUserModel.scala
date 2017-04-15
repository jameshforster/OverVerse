package models.auth

import play.api.libs.json.{Json, OFormat}

/**
  * Created by james-forster on 15/04/17.
  */
case class NewUserModel (username: String, email: String, password: String)

object NewUserModel {
  implicit val jsonFormat: OFormat[NewUserModel] = Json.format[NewUserModel]
}
