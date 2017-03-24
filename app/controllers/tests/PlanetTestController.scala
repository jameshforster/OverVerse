package controllers.tests

import com.google.inject.{Inject, Singleton}
import models.coordinates.PlanetCoordinateModel
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import services.PlanetService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Created by james-forster on 24/03/17.
  */
@Singleton
class PlanetTestController @Inject()(planetService: PlanetService) extends Controller {

  val createPlanet: Action[AnyContent] = Action.async { implicit request =>
    Try(request.body.asJson.get.as[PlanetCoordinateModel]) match {
      case Success(coordinates) => planetService.generatePlanet(coordinates).map { planet =>
        Ok(Json.toJson(planet))
      }.recoverWith {
        case exception => Future.successful(InternalServerError(exception.getMessage))
      }
      case Failure(exception) => Future.successful(BadRequest(exception.getMessage))
    }
  }
}
