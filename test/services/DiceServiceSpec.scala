package services

import helpers.TestSpec
import org.scalatestplus.play.OneAppPerSuite

/**
  * Created by Overlord59 on 22/03/2017.
  */
class DiceServiceSpec extends TestSpec with OneAppPerSuite {

  lazy val service: DiceService = app.injector.instanceOf[DiceService]

  def isBetween(lower: Int, upper: Int): Int => Boolean = { value =>
    value >= lower && value <= upper
  }

  "Calling .rollD6" should {

    "return an integer between 1 and 6" in {
      val result = isBetween(1, 6) {service.rollD6()}

      result shouldBe true
    }

    "not return an integer greater than 6" in {
      val result = service.rollD6() > 6

      result shouldBe false
    }

    "not return an integer less than 1" in {
      val result = service.rollD6() < 1

      result shouldBe false
    }
  }

  "Calling .rollD10" should {

    "return an integer between 1 and 10" in {
      val result = isBetween(1, 10) {service.rollD10()}

      result shouldBe true
    }

    "not return an integer greater than 10" in {
      val result = service.rollD6() > 10

      result shouldBe false
    }

    "not return an integer less than 1" in {
      val result = service.rollD6() < 1

      result shouldBe false
    }
  }
}
