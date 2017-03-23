package services

import com.google.inject.{Inject, Singleton}
import models.Attribute
import models.planet.EnvironmentModel

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Overlord59 on 22/03/2017.
  */
@Singleton
class PlanetService @Inject()(diceService: DiceService) {

  def generatePlanetSize(): Future[Int] = diceService.rollDX(8, 3)

  def generateAttribute(key: String): Future[Attribute] = {
    diceService.rollDX(6).map { value =>
      Attribute(key, value)
    }
  }

  def generateEnvironment(attributes: Seq[Attribute]): Future[EnvironmentModel] = {

    val validateEnvironment: EnvironmentModel => Boolean = {
      _.requirements.forall {
        _.apply(attributes)
      }
    }

    def randomiseEnvironment(environments: Seq[EnvironmentModel]): Future[EnvironmentModel] = {
      diceService.rollDX(environments.length).map { index =>
        environments.apply(index)
      }
    }

    val validEnvironments = EnvironmentModel.allEnvironments.filter(validateEnvironment)

    randomiseEnvironment(validEnvironments)
  }
}
