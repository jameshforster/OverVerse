package models.auth

import play.api.libs.json.{Json, OFormat}

/**
  * Created by james-forster on 15/04/17.
  */
case class LoginModel(username: String, password: String)

object LoginModel {
  implicit val jsonFormat: OFormat[LoginModel] = Json.format[LoginModel]
}
