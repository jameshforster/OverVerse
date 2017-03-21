package models.system

import models.coordinates.PlanetCoordinateModel
import helpers.TestSpec
import models.Attribute
import models.planet.Environment

/**
  * Created by Overlord59 on 15/03/2017.
  */
class SystemPlanetModelSpec extends TestSpec {

  "The SystemPlanetModel" should {

    "throw an exception when an invalid size is used" in {
      val exception = intercept[Exception] {
        SystemPlanetModel(mock[PlanetCoordinateModel], 0, mock[Environment], Attribute("Volatility", 1))()
      }

      exception.getMessage shouldBe "requirement failed: A size of 0 is invalid for a planet."
    }
  }
}
