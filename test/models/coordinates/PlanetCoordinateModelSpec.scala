package models.coordinates

import helpers.TestSpec
import play.api.libs.json.Json

/**
  * Created by james-forster on 14/03/17.
  */
class PlanetCoordinateModelSpec extends TestSpec {

  "The PlanetCoordinateModel" should {
    val model = PlanetCoordinateModel(SystemCoordinateModel(1, 2, 3, 4), 5)

    "display the correct format when converted to a string" in {
      model.toString shouldBe "1.2,3.4,5"
    }

    "display the correct format when converted to Json" in {
      Json.toJson(model).toString() shouldBe """{"systemCoordinateModel":{"X":1,"Y":2,"x":3,"y":4},"z":5}"""
    }
  }
}
