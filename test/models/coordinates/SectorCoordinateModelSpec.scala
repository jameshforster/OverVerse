package models.coordinates

import helpers.TestSpec

/**
  * Created by james-forster on 21/03/17.
  */
class SectorCoordinateModelSpec extends TestSpec {

  "The SectorCoordinateModel" should {
    val model = SectorCoordinateModel(1, 2)

    "display the correct format when converted to a string" in {
      model.toString shouldBe "{1.2}"
    }
  }
}
