package controllers.admin

import com.google.inject.{Inject, Singleton}
import models.universe.NewUniverseModel
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller, Result}
import services.{AdminService, UniverseService}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Overlord59 on 30/03/2017.
  */

@Singleton
class CreationController @Inject()(universeService: UniverseService, adminService: AdminService) extends Controller {

  val newUniverse: Action[AnyContent] = Action.async { implicit request =>

    def errorResponse(error: Throwable): Future[Result] = Future.successful(InternalServerError(Json.toJson(s"Unexpected error occurred: ${error.getMessage}")))
    def badRequestResponse(error: Throwable): Future[Result] = Future.successful(BadRequest(Json.toJson(s"Could not bind request body to json due to: ${error.getMessage}")))
    def successResponse(result: Unit): Future[Result] = Future.successful(NoContent)

    Try(request.body.asJson.get.as[NewUniverseModel]) match {
      case Success(model) => {
        for {
          universe <- universeService.generateUniverse(model.size)
          save <- adminService.storeUniverse(model.universeName, universe)
          response <- successResponse(save)
        } yield response
      }.recoverWith{
        case e: Exception => errorResponse(e)
      }
      case Failure(error) => badRequestResponse(error)
    }
  }
}
