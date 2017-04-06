package models.auth

import java.time.LocalDateTime

import play.api.libs.json.{Json, OFormat}

/**
  * Created by Overlord59 on 05/04/2017.
  */
case class AuthTokenModel (token: Map[String, String], expiresOn: LocalDateTime)

object AuthTokenModel {
  implicit val jsonFormat: OFormat[AuthTokenModel] = Json.format[AuthTokenModel]
}
