package models.planet

import models.Attribute
import play.api.libs.json.{Json, OFormat}

/**
  * Created by Overlord59 on 14/03/2017.
  */
case class Environment(name: String, attributes: Seq[Attribute])

object Environment {
  implicit val asJson: OFormat[Environment] = Json.format[Environment]
}
