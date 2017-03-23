package services

import com.google.inject.{Inject, Singleton}
import models.Attribute
import models.coordinates.PlanetCoordinateModel
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

  def generateTemperature(solar: Int): Future[Attribute] = {
    diceService.rollDX(3, solar).map { result =>
      Attribute("Temperature", result)
    }
  }

  def generatePrimaryAttributes(coordinates: PlanetCoordinateModel): Future[Seq[Attribute]] = {
    val solar = Attribute("Solar", 5 - coordinates.z)
    val getAtmosphere = generateAttribute("Atmosphere")
    val getMetal = generateAttribute("Metal")
    val getFuel = generateAttribute("Fuel")
    val getNuclear = generateAttribute("Nuclear")
    val getVolatility = generateAttribute("Volatility")

    for {
      atmosphere <- getAtmosphere
      metal <- getMetal
      fuel <- getFuel
      nuclear <- getNuclear
      volatility <- getVolatility
    } yield Seq(solar, atmosphere, metal, fuel, nuclear, volatility)
  }

  def generateSecondaryAttributes(attributes: Seq[Attribute]): Future[Seq[Attribute]] = {
    val getTemperature = diceService.rollDX(3, attributes.find{_.key == "Solar"}.map{_.value}.getOrElse(0)).map {
      case result if result > 5 => Attribute("Temperature", 5)
      case result => Attribute("Temperature", result)
    }
    val getWind = attributes.find{_.key == "Atmosphere"}.map{_.value}.getOrElse(0) match {
      case 2|3|4 => diceService.rollDX(3, 2).map { result => Attribute("Wind", result) }
      case atmosphere => Future.successful(Attribute("Wind", atmosphere))
    }

    for {
      temperature <- getTemperature
      wind <- getWind
    } yield attributes ++ Seq(temperature, wind)
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
