package models

import play.api.libs.json.{Json, OFormat}

/**
  * Created by Overlord59 on 14/03/2017.
  */
case class Attribute(key: String, value: Int) {
  Validation.validateAttribute(this)
}

object Attribute {
  implicit val asJson: OFormat[Attribute] = Json.format[Attribute]
}