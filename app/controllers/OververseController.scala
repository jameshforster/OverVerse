package controllers

import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Overlord59 on 03/04/2017.
  */
trait OververseController extends Controller {
  private[controllers] def errorResponse(error: Throwable): Future[Result] = Future {InternalServerError(Json.toJson(s"Unexpected error occurred: ${error.getMessage}"))}
  private[controllers] def badRequestResponse(error: Throwable): Future[Result] = Future {BadRequest(Json.toJson(s"Could not bind request body to json due to: ${error.getMessage}"))}
  private[controllers] def notFound(identifier: String): Future[Result] = Future {NotFound(Json.toJson(s"Resource: $identifier does not exist."))}
  private[controllers] def okResponse[T](model: T)(implicit writes: Writes[T]): Future[Result] = Future {Ok(Json.toJson(model))}
  private[controllers] def successResponse(result: Unit): Future[Result] = Future {NoContent}

  private[controllers] def boundAction[T](block: T => Future[Result])(implicit reads: Reads[T]): Action[AnyContent] = {
    Action.async { implicit request =>
      val action = Try {
        request.body.asJson.get.as[T]
      } match {
        case Success(t) => block(t)
        case Failure(exception) => badRequestResponse(exception)
      }
      action.recoverWith {
        case exception: Exception => errorResponse(exception)
      }
    }
  }
}
