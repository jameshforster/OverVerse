package controllers

import helpers.TestSpec
import org.scalatestplus.play.OneAppPerSuite
import random.DiceRoller

/**
  * Created by Overlord59 on 20/03/2017.
  */
class SkillControllerSpec extends TestSpec with OneAppPerSuite {

  def setupController(result: Int): SkillController = {
    val roller = new DiceRoller {
      override def rollD6(): Int = result
    }

    new SkillController(roller)
  }

  "Calling .rollDice" should {

    "return a sequence of values" in {
      lazy val controller = setupController(4)
      lazy val result = controller.rollDice(2)

      result shouldBe Seq(4, 4)
    }
  }

  "Calling .rollAllDice" should {

    "return a sequence" when {
      lazy val controller = setupController(4)

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
}
