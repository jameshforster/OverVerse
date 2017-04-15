package models.auth

import play.api.libs.json.{Json, OFormat}

/**
  * Created by james-forster on 15/04/17.
  */
case class TokenResponseModel(username: String, token: String)

object TokenResponseModel {
  implicit val jsonFormat: OFormat[TokenResponseModel] = Json.format[TokenResponseModel]
}