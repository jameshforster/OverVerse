package models.planet

import models.Attribute
import play.api.libs.json.{Json, OFormat}

/**
  * Created by Overlord59 on 14/03/2017.
  */
case class Environment(name: String, attributes: Seq[Attribute], requirements: Seq[Seq[Attribute] => Boolean])

object Environment {
  implicit val jsonFormat: OFormat[Environment] = Json.format[Environment]

  val barren = Environment("Barren", Seq(), Seq())

  val environments = Seq(barren)
}
