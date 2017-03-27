package services

import com.google.inject.{Inject, Singleton}
import models.coordinates.PlanetCoordinateModel
import models.system.{CategoryModel, StarModel, SystemPlanetModel}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Overlord59 on 25/03/2017.
  */
@Singleton
class SystemService @Inject()(planetService: PlanetService, diceService: DiceService) {

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
