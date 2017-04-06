package models.auth

import play.api.libs.json.{Json, OFormat}

/**
  * Created by Overlord59 on 05/04/2017.
  */
case class UserDetailsModel(username: String,
                            email: String,
                            password: Map[String, String],
                            isActivated: Boolean = false,
                            token: Option[AuthTokenModel] = None)

object UserDetailsModel {
  implicit val jsonFormat: OFormat[UserDetailsModel] = Json.format[UserDetailsModel]
}

