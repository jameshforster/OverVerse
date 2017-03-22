package services

import com.google.inject.{Inject, Singleton}
import models.Attribute
import models.planet.{EnvironmentModel, PlanetModel}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Overlord59 on 22/03/2017.
  */
@Singleton
class PlanetService @Inject()(diceService: DiceService) {

  def generateNewPlanet(): Future[PlanetModel] = {
    ???
  }

  def generatePlanetSize(): Future[Int] = {
    ???
  }

  def generateAttribute(key: String): Future[Attribute] = {
    ???
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
