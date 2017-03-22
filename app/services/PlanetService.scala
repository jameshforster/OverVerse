package services

import com.google.inject.{Inject, Singleton}
import models.Attribute
import models.planet.{Environment, PlanetModel}

/**
  * Created by Overlord59 on 22/03/2017.
  */
@Singleton
class PlanetService @Inject()(diceService: DiceService) {

  def generateNewPlanet(): PlanetModel = {
    ???
  }

  def generatePlanetSize(): Int = {
    ???
  }

  def generateAttribute(key: String): Attribute = {
    ???
  }

  def generateEnvironment(attributes: Seq[Attribute]): Environment = {

    val validateEnvironment: Environment => Boolean = {
      _.requirements.forall {
        _.apply(attributes)
      }
    }

    def randomiseEnvironment(environments: Seq[Environment]) = {
      val index = diceService.rollDX(environments.length)
      environments.apply(index)
    }

    val validEnvironments = Environment.environments.filter(validateEnvironment)

    randomiseEnvironment(validEnvironments)
  }
}
