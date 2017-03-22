package models.planet

import models.Attribute
import play.api.libs.json._

/**
  * Created by Overlord59 on 14/03/2017.
  */
case class EnvironmentModel(name: String, attributes: Seq[Attribute], requirements: Seq[Seq[Attribute] => Boolean])

object EnvironmentModel {
  def apply(name: String): EnvironmentModel = {
    allEnvironments.find(_.name == name).getOrElse(barren)
  }

  def hasAttribute(name: String, max: Int = 5, min: Int = 0): Seq[Attribute] => Boolean = { attributes =>
    attributes.find(_.key == name).exists { attribute =>
      attribute.value >= min && attribute.value <= max
    }
  }

  implicit val formatter: OFormat[EnvironmentModel] = new OFormat[EnvironmentModel] {
    override def writes(o: EnvironmentModel): JsObject = Json.obj("name" -> o.name)

    override def reads(json: JsValue): JsResult[EnvironmentModel] = {
      val name = (json \ "name").validate[String]
      name.map(name => EnvironmentModel(name))
    }
  }

  val barren = EnvironmentModel("Barren", Seq(), Seq())
  val mountainous = EnvironmentModel("Mountainous", Seq(), Seq(hasAttribute("Atmosphere", max = 1), hasAttribute("Fertility", max = 2)))

  val allEnvironments = Seq(barren, mountainous)
}
