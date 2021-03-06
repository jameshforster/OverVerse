package models

import helpers.TestSpec

/**
  * Created by Overlord59 on 15/03/2017.
  */
class ValidationSpec extends TestSpec {

  "Creating an attribute" when {

    "using a valid value of 0" should {
      val attribute = Attribute("Volatility", 0)

      "have the key for Volatility" in {
        attribute.key shouldBe "Volatility"
      }

      "have the value '0'" in {
        attribute.value shouldBe 0
      }
    }

    "using a valid value of 5" should {
      val attribute = Attribute("Volatility", 5)

      "have the key for Volatility" in {
        attribute.key shouldBe "Volatility"
      }

      "have the value '5'" in {
        attribute.value shouldBe 5
      }
    }

    "using a value below 0" should {
      val exception = intercept[Exception] {
        Attribute("Volatility", -1)
      }

      "have the correct exception" in {
        exception.getMessage shouldBe "requirement failed: The value -1 is not valid for the attribute Volatility."
      }
    }

    "using a value above 5" should {
      val exception = intercept[Exception] {
        Attribute("Volatility", 6)
      }

      "have the correct exception" in {
        exception.getMessage shouldBe "requirement failed: The value 6 is not valid for the attribute Volatility."
      }
    }
  }

  "Calling .validateSize" when {

    "using a valid value of 1" should {
      val result = Validation.validateSize(1)

      "return a Unit" in {
        result shouldBe {}
      }
    }

    "using a valid value of 10" should {
      val result = Validation.validateSize(10)

      "return a Unit" in {
        result shouldBe {}
      }
    }

    "using an invalid value of 0" should {
      val exception = intercept[Exception] {
        Validation.validateSize(0)
      }

      "return the correct exception" in {
        exception.getMessage shouldBe "requirement failed: A size of 0 is invalid for a planet."
      }
    }

    "using an invalid value of 11" should {
      val exception = intercept[Exception] {
        Validation.validateSize(11)
      }

      "return the correct exception" in {
        exception.getMessage shouldBe "requirement failed: A size of 11 is invalid for a planet."
      }
    }
  }
}
