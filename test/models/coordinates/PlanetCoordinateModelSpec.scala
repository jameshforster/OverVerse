package models.coordinates

import helpers.TestSpec
import play.api.libs.json.Json

/**
  * Created by james-forster on 14/03/17.
  */
class PlanetCoordinateModelSpec extends TestSpec {

  "The PlanetCoordinateModel" should {
    val model = PlanetCoordinateModel(SystemCoordinateModel(SectorCoordinateModel(1, 2), 3, 4), 5)

    "display the correct format when converted to a string" in {
      model.toString shouldBe "{1.2}{3.4}{5}"
    }

    "create an equivalent model using the overloaded apply" in {
      val applied = PlanetCoordinateModel(1, 2, 3, 4, 5)

      applied shouldBe model
    }
  }
}
