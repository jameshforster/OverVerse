package controllers

import models.exceptions._
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc._
import services.auth.AuthorisationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Created by Overlord59 on 03/04/2017.
  */
trait OververseController extends Controller {

  val authorisationService: AuthorisationService

  private[controllers] def errorResponse(error: Throwable): Future[Result] = Future {
    InternalServerError(Json.toJson(s"Unexpected error occurred: ${error.getMessage}"))
  }

  private[controllers] def badRequestResponse(error: Throwable): Future[Result] = Future {
    BadRequest(Json.toJson(s"Could not bind request body to json due to: ${error.getMessage}"))
  }

  private[controllers] def incorrectRequest(identifier: String): Future[Result] = Future {
    BadRequest(Json.toJson(s"$identifier request does not match existing records"))
  }

  private[controllers] def invalidHeaderResponse(): Future[Result] = Future {
    Unauthorized(Json.toJson("User is not logged in."))
  }

  private[controllers] def insufficientPermissionException(userLevel: Int, requiredLevel: Int): Future[Result] = Future {
    Forbidden(Json.toJson(s"A user with auth level $userLevel requires a level of $requiredLevel to use this resource."))
  }

  private[controllers] def notFound(identifier: String): Future[Result] = Future {
    NotFound(Json.toJson(s"Resource: $identifier does not exist."))
  }

  private[controllers] def okResponse[T](model: T)(implicit writes: Writes[T]): Future[Result] = Future {
    Ok(Json.toJson(model))
  }

  private[controllers] def successResponse(result: Unit): Future[Result] = Future {
    NoContent
  }

  private[controllers] def duplicateResponse(username: String) = Future {
    Conflict(Json.toJson(s"A user with the username: $username already exists."))
  }

  private[controllers] def boundAction[T](block: T => Future[Result])(implicit reads: Reads[T], request: Request[AnyContent]): Future[Result] = {
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

//  private[controllers] def authorisedUserAction(block: Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
//    val headers = request.headers.toMap
//    val username = Try(headers("username").head)
//    val token = Try(headers("token").head)
//    (username, token) match {
//      case (Success(name), Success(authToken)) => authorisationService.validateUser(name, authToken).flatMap(_ => block)
//        .recoverWith{
//          case _ => invalidHeaderResponse()
//        }
//      case _ => invalidHeaderResponse()
//    }
//  }

  private[controllers] def authorisedAdminAction(block: Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
    val headers = request.headers.toMap
    val username = Try(headers("username").head)
    val token = Try(headers("token").head)
    (username, token) match {
      case (Success(name), Success(authToken)) => authorisationService.validateAdmin(name, authToken).flatMap(_ => block)
        .recoverWith{
          case e: InsufficientPermissionException => insufficientPermissionException(e.userLevel, e.requiredLevel)
          case _ => invalidHeaderResponse()
        }
      case _ => invalidHeaderResponse()
    }
  }
}
