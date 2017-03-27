package services

import com.google.inject.{Inject, Singleton}
import models.coordinates.{PlanetCoordinateModel, SystemCoordinateModel}
import models.system.{CategoryModel, StarModel, SystemModel, SystemPlanetModel}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Overlord59 on 25/03/2017.
  */
@Singleton
class SystemService @Inject()(planetService: PlanetService, diceService: DiceService) {

  def createSystem(systemCoordinateModel: SystemCoordinateModel): Future[SystemModel] = {
    val getStar = createStar()

    def createPlanets(starModel: StarModel, position: Int = 0): Future[Seq[SystemPlanetModel]] = {
      if (position < 5) {
        for {
          planets <- createPlanets(starModel, position + 1)
          planet <- createSystemSlot(PlanetCoordinateModel(systemCoordinateModel, position), starModel)
        } yield {planet ++ planets}
      } else {
        createSystemSlot(PlanetCoordinateModel(systemCoordinateModel, position), starModel)
      }
    }

    for {
      star <- getStar
      planets <- createPlanets(star)
    } yield SystemModel(systemCoordinateModel, star, planets, Seq())
  }

  def createSystemSlot(planetCoordinateModel: PlanetCoordinateModel, starModel: StarModel): Future[Seq[SystemPlanetModel]] = {
    isSlotFilled(starModel).flatMap {
      case true => createPlanet(planetCoordinateModel).map{planet => Seq(planet)}
      case false => Future.successful(Seq())
    }
  }

  def isSlotFilled(starModel: StarModel): Future[Boolean] = {
    val target = 5 + starModel.size - starModel.age

    def validate(result: Int): Future[Boolean] = Future.successful(result >= target)

    for {
      dice <- diceService.rollDX(20)
      result <- validate(dice)
    } yield result
  }

  def createPlanet(coordinates: PlanetCoordinateModel): Future[SystemPlanetModel] = {
    planetService.generatePlanet(coordinates).map { planet =>
      SystemPlanetModel(planet.coordinate, planet.size, planet.environment)
    }
  }

  def createStar(): Future[StarModel] = {
    val getSize = determineSize()
    val getAge = determineAge()

    for {
      size <- getSize
      age <- getAge
      category <- determineCategory(size, age)
    } yield StarModel(size, age, category)
  }

  def determineSize(): Future[Int] = {
    diceService.rollDX(6, 1)
  }

  def determineAge(): Future[Int] = {
    diceService.rollDX(5, 1)
  }

  def determineCategory(size: Int, age: Int): Future[CategoryModel] = {

    val validateCategories: CategoryModel => Boolean = {
      _.conditions.forall {
        _.apply(size, age)
      }
    }

    def randomiseCategory(categories: Seq[CategoryModel]): Future[CategoryModel] = {
      diceService.rollDX(categories.length).map { index =>
        categories.apply(index)
      }
    }

    val validCategories = CategoryModel.allCategories.filter(validateCategories)

    randomiseCategory(validCategories)
  }
}
