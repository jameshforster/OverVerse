package controllers.tests

import com.google.inject.{Inject, Singleton}
import models.coordinates.{PlanetCoordinateModel, SystemCoordinateModel}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import services.{PlanetService, SystemService, UniverseService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Created by james-forster on 24/03/17.
  */
@Singleton
class TestGeneratorsController @Inject()(planetService: PlanetService, systemService: SystemService, universeService: UniverseService) extends Controller {

  val createPlanet: Action[AnyContent] = Action.async { implicit request =>
    Try(request.body.asJson.get.as[PlanetCoordinateModel]) match {
      case Success(coordinates) => planetService.generatePlanet(coordinates).map { planet => Ok(Json.toJson(planet)) }
        .recoverWith {
          case exception => Future.successful(InternalServerError(Json.toJson(s"Unexpected error occurred: ${exception.getMessage}")))
        }
      case Failure(exception) => Future.successful(BadRequest(Json.toJson(s"Could not bind request body to json due to: ${exception.getMessage}")))
    }
  }

  val createSystem: Action[AnyContent] = Action.async { implicit request =>
    Try(request.body.asJson.get.as[SystemCoordinateModel]) match {
      case Success(coordinates) => systemService.generateSystem(coordinates).map { system => Ok(Json.toJson(system)) }
        .recoverWith {
          case exception => Future.successful(InternalServerError(Json.toJson(s"Unexpected error occurred: ${exception.getMessage}")))
        }
      case Failure(exception) => Future.successful(BadRequest(Json.toJson(s"Could not bind request body to json due to: ${exception.getMessage}")))
    }
  }

  val createUniverse: Int => Action[AnyContent] = size => Action.async { implicit request =>
    universeService.generateUniverse(size).map { universe => Ok(Json.toJson(universe))}
      .recoverWith{
        case exception => Future.successful(InternalServerError(Json.toJson(s"Unexpected error occurred: ${exception.getMessage}")))
      }
  }
}
