package services

import com.google.inject.{Inject, Singleton}

import scala.util.Random

/**
  * Created by Overlord59 on 20/03/2017.
  */

@Singleton
class DiceService @Inject()() {

  def rollD6(): Int = {
    Random.nextInt(6) + 1
  }

  def rollD10(): Int = {
    Random.nextInt(10) + 1
  }

  def rollDX(x: Int): Int = {
    Random.nextInt(x)
  }
}
