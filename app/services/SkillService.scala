package services

import com.google.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Overlord59 on 20/03/2017.
  */
@Singleton
class SkillService @Inject()(diceRoller: DiceService) {

  def skillCheck(dice: Int, bonus: Int, passValue: Int): Future[Int] = {
    val getDiceRolls = rollAllDice(dice)
    val getSkillBonus = getSkillBonusValue(bonus)

    for {
      diceRolls <- getDiceRolls
      skillBonus <- getSkillBonus
      diceResults <- getDiceValue(diceRolls, passValue)
    } yield {
      diceResults + skillBonus
    }
  }

  def getDiceValue(diceRolls: Seq[Int], passValue: Int): Future[Int] = {
    Future.successful(diceRolls.count(_ >= passValue))
  }

  def getSkillBonusValue(bonus: BigDecimal): Future[Int] = {
    getPartialSkillBonus(bonus).map { partialBonus =>
      val result = bonus / 10 + partialBonus
      result.bigDecimal.intValue()
    }
  }

  def getPartialSkillBonus(bonus: BigDecimal): Future[Int] = {
    val partial = bonus.bigDecimal.remainder(BigDecimal(10))

    diceRoller.rollD10().map { value =>
      if (value <= partial) 1
      else 0
    }
  }

  def rollAllDice(dice: Int, current: Seq[Int] = Seq()): Future[Seq[Int]] = {
    if (dice > 0) {
      rollDice(dice).flatMap { results =>
        rollAllDice(results.count(_.equals(6)), current ++ results)
      }
    } else Future.successful(current)
  }

  def rollDice(dice: Int): Future[Seq[Int]] = {

    if (dice > 1) {
      val individualDiceResult = diceRoller.rollD6().map{ x => Seq(x)}
      val remainingDiceResults = rollDice(dice - 1)

      for {
        remainingDice <- remainingDiceResults
        rolledDice <- individualDiceResult
      } yield {
        remainingDice ++ rolledDice
      }
    }
    else diceRoller.rollD6().map{ x => Seq(x)}
  }
}
