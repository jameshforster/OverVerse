package services

import helpers.TestSpec
import org.scalatestplus.play.OneAppPerSuite

/**
  * Created by Overlord59 on 20/03/2017.
  */
class SkillServiceSpec extends TestSpec with OneAppPerSuite {

  def setupController(d6Result: Int, d10Result: Int): SkillService = {
    val roller = new DiceService {
      override def rollD6(): Int = d6Result
      override def rollD10(): Int = d10Result
    }

    new SkillService(roller)
  }

  "Calling .rollDice" should {

    "return a sequence of values" in {
      lazy val controller = setupController(4, 0)
      lazy val result = controller.rollDice(2)

      result shouldBe Seq(4, 4)
    }
  }

  "Calling .rollAllDice" should {

    "return a sequence" when {
      lazy val controller = setupController(4, 0)

      "a current sequence is not provided" in {
        lazy val result = controller.rollAllDice(2)()

        result shouldBe Seq(4, 4)
      }

      "a current sequence is provided" in {
        lazy val result = controller.rollAllDice(2)(Seq(1, 2, 3, 4, 5, 6, 6))

        result shouldBe Seq(1, 2, 3, 4, 5, 6, 6, 4, 4)
      }
    }
  }

  "Calling .getDiceValue" should {

    "return a value of 4" when {

      "using a pass value of 4 with 4 rolls of 4 or above" in {
        val rolls = Seq(1, 3, 5, 4, 2, 6, 5)
        lazy val controller = setupController(0, 0)
        lazy val result = controller.getDiceValue(rolls, 4)

        result shouldBe 4
      }

      "using a pass value of 3 with 4 rolls above 3" in {
        val rolls = Seq(3, 6, 1, 2, 4, 4)
        lazy val controller = setupController(0, 0)
        lazy val result = controller.getDiceValue(rolls, 3)

        result shouldBe 4
      }
    }

    "return a value of 1" when {

      "using a pass value of 6 with one roll of a 6" in {
        val rolls = Seq(1, 3, 5, 4, 2, 6, 5)
        lazy val controller = setupController(0, 0)
        lazy val result = controller.getDiceValue(rolls, 6)

        result shouldBe 1
      }
    }
  }

  "Calling .getPartialSkillBonus" should {

    "return a value of 1" when {

      "provided with a value of 6 and a dice roll of 6" in {
        lazy val controller = setupController(0, 6)
        lazy val result = controller.getPartialSkillBonus(6)

        result shouldBe 1
      }

      "provided with a value of 5 and a dice roll of 5" in {
        lazy val controller = setupController(0, 5)
        lazy val result = controller.getPartialSkillBonus(5)

        result shouldBe 1
      }
    }

    "return a value of 0" when {

      "provided with a value of 6 and a dice roll of 7" in {
        lazy val controller = setupController(0, 7)
        lazy val result = controller.getPartialSkillBonus(6)

        result shouldBe 0
      }

      "provided with a value of 5 and a dice roll of 6" in {
        lazy val controller = setupController(0, 6)
        lazy val result = controller.getPartialSkillBonus(5)

        result shouldBe 0
      }
    }
  }

  "Calling .getSkillBonusValue" should {

    "return a value of 5" when {

      "provided with a value of 50" in {
        lazy val controller = setupController(0, 1)
        lazy val result = controller.getSkillBonusValue(50)

        result shouldBe 5
      }

      "provided with a value of 45 and a dice roll of 5" in {
        lazy val controller = setupController(0, 5)
        lazy val result = controller.getSkillBonusValue(45)

        result shouldBe 5
      }
    }

    "return a value of 4" when {
      "provided with a value of 40" in {
        lazy val controller = setupController(0, 1)
        lazy val result = controller.getSkillBonusValue(40)

        result shouldBe 4
      }

      "provided with a value of 45 and a dice roll of 6" in {
        lazy val controller = setupController(0, 6)
        lazy val result = controller.getSkillBonusValue(45)

        result shouldBe 4
      }
    }
  }

  "Calling .skillCheck" should {

    "return a value of 4" in {
      lazy val controller = setupController(4, 6)
      lazy val result = controller.skillCheck(2, 21, 4)

      result shouldBe 4
    }

    "return a value of 2" in {
      lazy val controller = setupController(4, 6)
      lazy val result = controller.skillCheck(2, 19, 5)

      result shouldBe 2
    }
  }
}