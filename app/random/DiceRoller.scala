package random

import com.google.inject.{Inject, Singleton}

import scala.util.Random

/**
  * Created by Overlord59 on 20/03/2017.
  */

@Singleton
class DiceRoller @Inject()() {

  def rollD6(): Int = {
    Random.nextInt(6) + 1
  }
}
