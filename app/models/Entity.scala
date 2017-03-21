package models

import play.api.libs.json.{Json, OFormat}

/**
  * Created by james-forster on 21/03/17.
  */
case class Entity(name: String)

object Entity {
  implicit val asJson: OFormat[Entity] = Json.format[Entity]
}