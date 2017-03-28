package services

import com.google.inject.{Inject, Singleton}
import models.Attribute
import models.coordinates.PlanetCoordinateModel
import models.planet.{EnvironmentModel, PlanetModel}
import contants.AttributeKeys

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Overlord59 on 22/03/2017.
  */
@Singleton
class PlanetService @Inject()(diceService: DiceService) {

  def generatePlanet(coordinateModel: PlanetCoordinateModel): Future[PlanetModel] = {
    val getSize = generatePlanetSize()
    val getPrimaryAttributes = generatePrimaryAttributes(coordinateModel)

    for {
      size <- getSize
      primaryAttributes <- getPrimaryAttributes
      secondaryAttributes <- generateSecondaryAttributes(primaryAttributes)
      tertiaryAttributes <- generateTertiaryAttributes(secondaryAttributes)
      environment <- generateEnvironment(tertiaryAttributes)
    } yield PlanetModel(coordinateModel, size, environment, tertiaryAttributes)
  }

  def generatePlanetSize(): Future[Int] = diceService.rollDX(10, 1)

  def generateAttribute(key: String): Future[Attribute] = {
    diceService.rollDX(6).map { value =>
      Attribute(key, value)
    }
  }

  def extractAttributeValue(attributes: Seq[Attribute], key: String): Int = {
    attributes.find(_.key == key).map{_.value}.getOrElse(0)
  }

  def generatePrimaryAttributes(coordinates: PlanetCoordinateModel): Future[Seq[Attribute]] = {
    val solar = Attribute(AttributeKeys.solar, 5 - coordinates.z)
    val getAtmosphere = generateAttribute(AttributeKeys.atmosphere)
    val getMetal = generateAttribute(AttributeKeys.metal)
    val getFuel = generateAttribute(AttributeKeys.fuel)
    val getNuclear = generateAttribute(AttributeKeys.nuclear)
    val getVolatility = generateAttribute(AttributeKeys.volatility)

    for {
      atmosphere <- getAtmosphere
      metal <- getMetal
      fuel <- getFuel
      nuclear <- getNuclear
      volatility <- getVolatility
    } yield Seq(solar, atmosphere, metal, fuel, nuclear, volatility)
  }

  def generateSecondaryAttributes(attributes: Seq[Attribute]): Future[Seq[Attribute]] = {
    val getTemperature = diceService.rollDX(3, extractAttributeValue(attributes, AttributeKeys.solar)).map {
      case result if result > 5 => Attribute(AttributeKeys.temperature, 5)
      case result => Attribute(AttributeKeys.temperature, result)
    }
    val getWind = extractAttributeValue(attributes, AttributeKeys.atmosphere) match {
      case 2|3|4 => diceService.rollDX(3, 2).map { result => Attribute(AttributeKeys.wind, result) }
      case atmosphere => Future.successful(Attribute(AttributeKeys.wind, atmosphere))
    }

    for {
      temperature <- getTemperature
      wind <- getWind
    } yield attributes ++ Seq(temperature, wind)
  }

  def generateTertiaryAttributes(attributes: Seq[Attribute]): Future[Seq[Attribute]] = {
    val temperature = extractAttributeValue(attributes, AttributeKeys.temperature)
    val getWater = temperature match {
      case 4 => diceService.rollDX(1).map {result => Attribute(AttributeKeys.water, result)}
      case 5 => Future.successful(Attribute(AttributeKeys.water, 0))
      case _ => generateAttribute(AttributeKeys.water)
    }
    val getFertility = {
      val volatility = extractAttributeValue(attributes, AttributeKeys.volatility)
      val atmosphere = extractAttributeValue(attributes, AttributeKeys.atmosphere)

      (atmosphere, temperature, volatility) match {
        case (2|3, 2|3, _) => generateAttribute(AttributeKeys.fertility)
        case (y, _, x) if x > 3 && y < 5=> generateAttribute(AttributeKeys.fertility)
        case _ => Future.successful(Attribute(AttributeKeys.fertility, 0))
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

    randomiseEnvironment(if(validEnvironments.isEmpty) Seq(EnvironmentModel.barren) else validEnvironments)
  }
}
