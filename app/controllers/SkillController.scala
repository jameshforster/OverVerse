package controllers

import com.google.inject.{Inject, Singleton}
import random.DiceRoller

/**
  * Created by Overlord59 on 20/03/2017.
  */
@Singleton
class SkillController @Inject()(diceRoller: DiceRoller) {

  def rollAllDice(dice: Int)(current: Seq[Int] = Seq()): Seq[Int] = {
    if (dice > 0) {
      val results = current ++ rollDice(dice)
      rollAllDice(results.count(_.equals(6)))(results)
    } else current
  }

  def rollDice(dice: Int): Seq[Int] = {
    if (dice > 1) rollDice(dice - 1) ++ Seq(diceRoller.rollD6())
    else Seq(diceRoller.rollD6())
  }
}
