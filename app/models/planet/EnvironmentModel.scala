package models.planet

import contants.AttributeKeys
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
  val mountainous = EnvironmentModel("Mountainous", Seq(), Seq(hasAttribute(AttributeKeys.atmosphere, max = 1), hasAttribute(AttributeKeys.fertility, max = 2)))
  val cavern = EnvironmentModel("Cavern", Seq(), Seq(hasAttribute(AttributeKeys.volatility, max = 1), hasAttribute(AttributeKeys.water, min = 1)))
  val habitable = EnvironmentModel("Habitable", Seq(), Seq(hasAttribute(AttributeKeys.solar, max = 3, min = 0), hasAttribute(AttributeKeys.temperature, max = 4, min = 1),
    hasAttribute(AttributeKeys.nuclear, max = 3), hasAttribute(AttributeKeys.atmosphere, max = 2, min = 1), hasAttribute(AttributeKeys.fertility, min = 1),
    hasAttribute(AttributeKeys.water, min = 1)))
  val arable = EnvironmentModel("Arable", Seq(), Seq(hasAttribute(AttributeKeys.solar, max = 3, min = 1), hasAttribute(AttributeKeys.temperature, max = 3, min = 2),
    hasAttribute(AttributeKeys.nuclear, max = 2), hasAttribute(AttributeKeys.atmosphere, max = 3, min = 1), hasAttribute(AttributeKeys.fertility, min = 2),
    hasAttribute(AttributeKeys.water, min = 2)))
  val gaia = EnvironmentModel("Gaia", Seq(), Seq(hasAttribute(AttributeKeys.solar, max = 3, min = 1), hasAttribute(AttributeKeys.temperature, max = 3, min = 2),
    hasAttribute(AttributeKeys.nuclear, max = 2), hasAttribute(AttributeKeys.atmosphere, max = 2, min = 2), hasAttribute(AttributeKeys.fertility, min = 3),
    hasAttribute(AttributeKeys.water, min = 2)))
  val lush = EnvironmentModel("Lush", Seq(), Seq(hasAttribute(AttributeKeys.solar, max = 3, min = 2), hasAttribute(AttributeKeys.temperature, max = 3, min = 2),
    hasAttribute(AttributeKeys.nuclear, max = 2), hasAttribute(AttributeKeys.atmosphere, max = 3, min = 2), hasAttribute(AttributeKeys.fertility, min = 4),
    hasAttribute(AttributeKeys.water, min = 2)))
  val jungle = EnvironmentModel("Jungle", Seq(), Seq(hasAttribute(AttributeKeys.solar, max = 4, min = 2), hasAttribute(AttributeKeys.temperature, max = 4, min = 3),
    hasAttribute(AttributeKeys.nuclear, max = 3), hasAttribute(AttributeKeys.atmosphere, max = 3, min = 2), hasAttribute(AttributeKeys.fertility, min = 4),
    hasAttribute(AttributeKeys.water, min = 3)))
  val ocean = EnvironmentModel("Ocean", Seq(), Seq(hasAttribute(AttributeKeys.atmosphere, max = 4, min = 1), hasAttribute(AttributeKeys.water, min = 5)))
  val magma = EnvironmentModel("Magma", Seq(), Seq(hasAttribute(AttributeKeys.temperature, min = 5), hasAttribute(AttributeKeys.metal, min = 3),
    hasAttribute(AttributeKeys.volatility, min = 3), hasAttribute(AttributeKeys.atmosphere, max = 4, min = 3)))
  val volcanic = EnvironmentModel("Volcanic", Seq(), Seq(hasAttribute(AttributeKeys.temperature, min = 3), hasAttribute(AttributeKeys.volatility, min = 4),
    hasAttribute(AttributeKeys.atmosphere, max = 4, min = 2)))
  val desert = EnvironmentModel("Desert", Seq(), Seq(hasAttribute(AttributeKeys.atmosphere, max = 4, min = 1), hasAttribute(AttributeKeys.water, max = 1),
    hasAttribute(AttributeKeys.fertility, max = 1)))
  val frozen = EnvironmentModel("Frozen", Seq(), Seq(hasAttribute(AttributeKeys.atmosphere, max = 4, min = 1), hasAttribute(AttributeKeys.fertility, max = 1),
    hasAttribute(AttributeKeys.temperature, max = 1), hasAttribute(AttributeKeys.water, min = 5)))
  val arctic = EnvironmentModel("Arctic", Seq(), Seq(hasAttribute(AttributeKeys.atmosphere, max = 4, min = 1), hasAttribute(AttributeKeys.fertility, max = 1),
    hasAttribute(AttributeKeys.temperature, max = 1)))
  val gasGiant = EnvironmentModel("Gas Giant", Seq(), Seq(hasAttribute(AttributeKeys.atmosphere, min = 5)))

  val allEnvironments = Seq(barren, mountainous, cavern, habitable, arable, gaia, lush, jungle, ocean, magma, volcanic, desert, frozen, arctic, gasGiant)
}
