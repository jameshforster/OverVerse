package controllers

import com.google.inject.{Inject, Singleton}
import models.auth.{LoginModel, NewUserModel, TokenResponseModel}
import models.exceptions.{DuplicateUserException, IncorrectPasswordException, UserNotFoundException}
import play.api.mvc.{Action, AnyContent}
import services.auth.AuthorisationService

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by james-forster on 15/04/17.
  */
@Singleton
class AccountController @Inject()(val authorisationService: AuthorisationService) extends OververseController {

  val createUser: Action[AnyContent] = Action.async { implicit request =>
    boundAction[NewUserModel]{ user =>
      authorisationService.registerUser(user.username, user.email, user.password).flatMap {
        successResponse
      }.recoverWith {
        case _: DuplicateUserException => duplicateResponse(user.username)
        case e => errorResponse(e)
      }
    }
  }

  val loginUser: Action[AnyContent] = Action.async { implicit request =>
    boundAction[LoginModel] { login =>
      authorisationService.loginUser(login.username, login.password).flatMap { token =>
        okResponse(TokenResponseModel(login.username, token))
      }.recoverWith {
        case _: UserNotFoundException => notFound(login.username)
        case _: IncorrectPasswordException => incorrectRequest("password")
        case e => errorResponse(e)
      }
    }
  }
}
