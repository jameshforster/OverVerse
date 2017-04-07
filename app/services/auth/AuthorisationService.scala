package services.auth

import java.time.LocalDateTime

import com.google.inject.{Inject, Singleton}
import connectors.MongoConnector
import models.auth.{AuthTokenModel, UserDetailsModel}
import models.exceptions._
import org.apache.commons.codec.binary.Hex
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

/**
  * Created by Overlord59 on 05/04/2017.
  */

@Singleton
class AuthorisationService @Inject()(encryptionService: EncryptionService, mongoConnector: MongoConnector) {

  private def createToken(): AuthTokenModel = {
    val encoder = new Hex
    val token = new String(encoder.encode(Random.nextString(8).getBytes()))
    AuthTokenModel(token, LocalDateTime.now().plusMinutes(60))
  }

  private def setAuthToken(details: UserDetailsModel, newToken: AuthTokenModel): Future[Unit] = {
    val user = UserDetailsModel(details.username, details.email, details.password, details.isActivated, Some(newToken))
    mongoConnector.updateEntry[UserDetailsModel]("authorisation", "username", Json.toJson(details.username), user)
  }

  def loginUser(username: String, password: String): Future[String] = {

    def setNewAuthToken(details: UserDetailsModel): String = {
      val token = createToken()
      setAuthToken(details, token)
      token.token
    }

    val comparePassword: UserDetailsModel => String = details =>
      encryptionService.decrypt(details.password) match {
        case `password` => setNewAuthToken(details)
        case _ => throw new IncorrectPasswordException
      }

    mongoConnector.getEntry[UserDetailsModel]("authorisation", "username", Json.toJson(username)).map {
      details => comparePassword(details.getOrElse(throw new UserNotFoundException))
    }
  }

  def validateUser(username: String, authToken: String): Future[Unit] = {
    def compareToken(details: UserDetailsModel, token: AuthTokenModel): Unit = token match {
      case AuthTokenModel(`authToken`, expires) if expires.isAfter(LocalDateTime.now()) =>
        setRefreshedToken(details, token)
      case AuthTokenModel(`authToken`, _) => throw new TokenTimeoutException
      case _ => throw new InvalidTokenException
    }

    def setRefreshedToken(details: UserDetailsModel, token: AuthTokenModel): Future[Unit] = {
      val refreshedToken = AuthTokenModel(token.token, LocalDateTime.now().plusMinutes(60))
      setAuthToken(details, refreshedToken)
    }

    mongoConnector.getEntry[UserDetailsModel]("authorisation", "username", Json.toJson(username)).map {
      details => {
        val result = details.getOrElse(throw new UserNotFoundException)
        compareToken(result, result.token.getOrElse(throw new InvalidTokenException))
      }
    }
  }

  def registerUser(username: String, email: String, password: String): Future[Unit] = {
    val newUser = UserDetailsModel(username, email, encryptionService.encrypt(password))
    val checkUniqueUser: Option[UserDetailsModel] => Unit = {
      case Some(_) => throw new DuplicateUserException
      case _ => mongoConnector.putEntry[UserDetailsModel]("authorisation", newUser)
    }

    mongoConnector.getEntry[UserDetailsModel]("authorisation", "username", Json.toJson(username)).map {
      checkUniqueUser
    }
  }
}
