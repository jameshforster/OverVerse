package services

import com.google.inject.{Inject, Singleton}

import scala.concurrent.Future
import scala.util.Random

/**
  * Created by Overlord59 on 20/03/2017.
  */

@Singleton
class DiceService @Inject()() {

  def rollD6(): Future[Int] = {
    Future.successful(Random.nextInt(6) + 1)
  }

  def rollD10(): Future[Int] = {
    Future.successful(Random.nextInt(10) + 1)
  }

  def rollDX(x: Int): Future[Int] = {
    Future.successful(Random.nextInt(x))
  }
}
