package services

import com.google.inject.{Inject, Singleton}
import models.Attribute
import models.planet.{EnvironmentModel, PlanetModel}

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

  def generateEnvironment(attributes: Seq[Attribute]): EnvironmentModel = {

    val validateEnvironment: EnvironmentModel => Boolean = {
      _.requirements.forall {
        _.apply(attributes)
      }
    }

    def randomiseEnvironment(environments: Seq[EnvironmentModel]) = {
      val index = diceService.rollDX(environments.length)
      environments.apply(index)
    }

    val validEnvironments = EnvironmentModel.allEnvironments.filter(validateEnvironment)

    randomiseEnvironment(validEnvironments)
  }
}
