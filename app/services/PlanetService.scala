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
      val modified = if (result > 5) 5 else result
      Attribute("Temperature", modified)
    }
  }

  def extractAttributeValue(attributes: Seq[Attribute], key: String): Int = {
    attributes.find(_.key == key).map{_.value}.getOrElse(0)
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
    val getTemperature = diceService.rollDX(3, extractAttributeValue(attributes, "Solar")).map {
      case result if result > 5 => Attribute("Temperature", 5)
      case result => Attribute("Temperature", result)
    }
    val getWind = extractAttributeValue(attributes, "Atmosphere") match {
      case 2|3|4 => diceService.rollDX(3, 2).map { result => Attribute("Wind", result) }
      case atmosphere => Future.successful(Attribute("Wind", atmosphere))
    }

    for {
      temperature <- getTemperature
      wind <- getWind
    } yield attributes ++ Seq(temperature, wind)
  }

  def generateTertiaryAttributes(attributes: Seq[Attribute]): Future[Seq[Attribute]] = {
    val temperature = extractAttributeValue(attributes, "Temperature")
    val getWater = temperature match {
      case 4 => diceService.rollDX(1).map {result => Attribute("Water", result)}
      case 5 => Future.successful(Attribute("Water", 0))
      case _ => generateAttribute("Water")
    }
    val getFertility = {
      val volatility = extractAttributeValue(attributes, "Volatility")
      val atmosphere = extractAttributeValue(attributes, "Atmosphere")

      (atmosphere, temperature, volatility) match {
        case (2|3, 2|3, _) => generateAttribute("Fertility")
        case (_, _, x) if x > 3 => generateAttribute("Fertility")
        case _ => Future.successful(Attribute("Fertility", 0))
      }
    }

    for {
      water <- getWater
      fertility <- getFertility
    } yield attributes ++ Seq(water, fertility)
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
