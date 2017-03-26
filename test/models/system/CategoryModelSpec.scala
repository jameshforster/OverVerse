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
        val model = CategoryModel("Yellow Star")

        model shouldBe CategoryModel.yellowStar
      }

      "using an invalid category" in {
        val exception = intercept[Exception] {CategoryModel("")}

        exception.getMessage shouldBe "Invalid star category provided."
      }
    }

    "be convertible into a valid json value" in {
      val model = CategoryModel.yellowStar

      Json.toJson(model).toString() shouldBe """{"name":"Yellow Star"}"""
    }

    "create a valid model from a valid json value" in {
      val json = Json.obj("name" -> "Yellow Star")

      Json.fromJson[CategoryModel](json).get shouldBe CategoryModel.yellowStar
    }
  }

  "Calling .hasSize" should {

    "return a true with a size within the range" when {

      "at the upper limit" in {
        val result = CategoryModel.hasSize(4, 2)(StarModel(4, 1, CategoryModel.yellowStar))

        result shouldBe true
      }

      "at the lower limit" in {
        val result = CategoryModel.hasSize(4, 3)(StarModel(3, 1, CategoryModel.yellowStar))

        result shouldBe true
      }
    }

    "return a false with a size outside the range" when {

      "a result above the upper limit" in {
        val result = CategoryModel.hasSize(4, 2)(StarModel(5, 1, CategoryModel.yellowStar))

        result shouldBe false
      }

      "a result below the lower limit" in {
        val result = CategoryModel.hasSize(4, 2)(StarModel(1, 1, CategoryModel.yellowStar))

        result shouldBe false
      }
    }
  }

  "Calling .hasAge" should {

    "return a true with a size within the range" when {

      "at the upper limit" in {
        val result = CategoryModel.hasAge(4, 2)(StarModel(4, 4, CategoryModel.yellowStar))

        result shouldBe true
      }

      "at the lower limit" in {
        val result = CategoryModel.hasAge(4, 3)(StarModel(3, 3, CategoryModel.yellowStar))

        result shouldBe true
      }
    }

    "return a false with a size outside the range" when {

      "a result above the upper limit" in {
        val result = CategoryModel.hasAge(4, 2)(StarModel(5, 5, CategoryModel.yellowStar))

        result shouldBe false
      }

      "a result below the lower limit" in {
        val result = CategoryModel.hasAge(4, 2)(StarModel(1, 1, CategoryModel.yellowStar))

        result shouldBe false
      }
    }
  }
}
