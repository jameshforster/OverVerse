package services

import com.google.inject.{Inject, Singleton}

/**
  * Created by Overlord59 on 20/03/2017.
  */
@Singleton
class SkillService @Inject()(diceRoller: DiceService) {

  def skillCheck(dice: Int, bonus: Int, passValue: Int): Int = {
    getDiceValue(rollAllDice(dice)(), passValue) + getSkillBonusValue(BigDecimal(bonus))
  }

  def getDiceValue(diceRolls: Seq[Int], passValue: Int): Int = {
    diceRolls.count(_ >= passValue)
  }

  def getSkillBonusValue(bonus: BigDecimal): Int = {
    val result = bonus/10 + getPartialSkillBonus(bonus)
    result.bigDecimal.intValue()
  }

  def getPartialSkillBonus(bonus: BigDecimal): Int = {
    val partial = bonus.bigDecimal.remainder(BigDecimal(10))
    if (diceRoller.rollD10() <= partial) 1
    else 0
  }

  def rollAllDice(dice: Int)(current: Seq[Int] = Seq()): Seq[Int] = {
    if (dice > 0) {
      val results = rollDice(dice)
      rollAllDice(results.count(_.equals(6)))(current ++ results)
    } else current
  }

  def rollDice(dice: Int): Seq[Int] = {
    if (dice > 1) rollDice(dice - 1) ++ Seq(diceRoller.rollD6())
    else Seq(diceRoller.rollD6())
  }
}
