package models.coordinates

import helpers.TestSpec
import play.api.libs.json.Json

/**
  * Created by james-forster on 14/03/17.
  */
class SystemCoordinateModelSpec extends TestSpec {

  "The SystemCoordinateModel" should {
    val model = SystemCoordinateModel(SectorCoordinateModel(1, 2), 3, 4)

    "display the correct format when converted to a string" in {
      model.toString shouldBe "{1.2}{3.4}"
    }

    "create an equivalent model using the overloaded apply" in {
      val applied = SystemCoordinateModel(1, 2, 3, 4)

      applied shouldBe model
    }
  }
}
