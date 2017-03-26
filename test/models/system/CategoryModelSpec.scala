package models.system

import helpers.TestSpec
import play.api.libs.json.Json

/**
  * Created by Overlord59 on 26/03/2017.
  */
class CategoryModelSpec extends TestSpec {

  "The CategoryModel" should {

    "create an equivalent model using the overloaded apply" when {

      "using a valid category" in {
        val model = CategoryModel("Dwarf")

        model shouldBe CategoryModel.dwarf
      }

      "using an invalid category" in {
        val model = CategoryModel("")

        model shouldBe CategoryModel.star
      }
    }

    "be convertible into a valid json value" in {
      val model = CategoryModel.star

      Json.toJson(model).toString() shouldBe """{"name":"Star"}"""
    }

    "create a valid model from a valid json value" in {
      val json = Json.obj("name" -> "Dwarf")

      Json.fromJson[CategoryModel](json).get shouldBe CategoryModel.dwarf
    }
  }

  "Calling .hasSize" should {

    "return a true with a size within the range" when {

      "at the upper limit" in {
        val result = CategoryModel.hasSize(4, 2)(StarModel(4, 1, "", CategoryModel.star))

        result shouldBe true
      }

      "at the lower limit" in {
        val result = CategoryModel.hasSize(4, 3)(StarModel(3, 1, "", CategoryModel.star))

        result shouldBe true
      }
    }

    "return a false with a size outside the range" when {

      "a result above the upper limit" in {
        val result = CategoryModel.hasSize(4, 2)(StarModel(5, 1, "", CategoryModel.star))

        result shouldBe false
      }

      "a result below the lower limit" in {
        val result = CategoryModel.hasSize(4, 2)(StarModel(1, 1, "", CategoryModel.star))

        result shouldBe false
      }
    }
  }
}
