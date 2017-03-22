package models.planet

import helpers.TestSpec
import models.Attribute
import play.api.libs.json.Json

/**
  * Created by james-forster on 22/03/17.
  */
class EnvironmentModelSpec extends TestSpec {

  "The EnvironmentModel" should {

    "create an equivalent model using the overloaded apply" when {

      "using a valid environment" in {
        val model = EnvironmentModel("Mountainous")

        model shouldBe EnvironmentModel.mountainous
      }

      "using an invalid environment" in {
        val model = EnvironmentModel("")

        model shouldBe EnvironmentModel.barren
      }
    }

    "be convertible into a valid json value" in {
      val model = EnvironmentModel.barren

      Json.toJson(model).toString() shouldBe """{"name":"Barren"}"""
    }

    "create a valid model from a valid json value" in {
      val json = Json.obj("name" -> "Mountainous")

      Json.fromJson[EnvironmentModel](json).get shouldBe EnvironmentModel.mountainous
    }
  }

  "Calling .hasAttribute" should {
    val attributes = Seq(Attribute("firstKey", 1), Attribute("secondKey", 2), Attribute("thirdKey", 4))

    "return a true if the attribute is present" when {

      "within the default bands when not set" in {
        val result = EnvironmentModel.hasAttribute("secondKey")(attributes)

        result shouldBe true
      }

      "within the defined bands when set" in {
        val result = EnvironmentModel.hasAttribute("secondKey", max = 2, min = 2)(attributes)

        result shouldBe true
      }
    }

    "return a false" when {

      "no matching attribute is found" in {
        val result = EnvironmentModel.hasAttribute("fourthKey")(attributes)

        result shouldBe false
      }

      "a matching attribute above the defined maximum is found" in {
        val result = EnvironmentModel.hasAttribute("secondKey", max = 1)(attributes)

        result shouldBe false
      }

      "a matching attribute below the defined minimum is found" in {
        val result = EnvironmentModel.hasAttribute("secondKey", min = 3)(attributes)

        result shouldBe false
      }
    }
  }
}
