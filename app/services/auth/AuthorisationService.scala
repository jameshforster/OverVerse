package services.auth

import java.time.LocalDateTime

import com.google.inject.{Inject, Singleton}
import connectors.MongoConnector
import models.auth.{AuthTokenModel, UserDetailsModel}
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

  def loginUser(username: String, password: String): Future[Option[String]] = {

    def setNewAuthToken(details: UserDetailsModel): Option[String] = {
      val token = createToken()
      setAuthToken(details, token)
      Some(token.token)
    }

    val comparePassword: UserDetailsModel => Option[String] = details =>
      encryptionService.decrypt(details.password) match {
        case `password` => setNewAuthToken(details)
        case _ => None
      }

    mongoConnector.getEntry[UserDetailsModel]("authorisation", "username", Json.toJson(username)).map {
      _.flatMap {
        comparePassword
      }
    }
  }

  def validateUser(username: String, authToken: String): Future[Option[Boolean]] = {
    def compareToken(details: UserDetailsModel, token: AuthTokenModel): Boolean = token match {
      case AuthTokenModel(`authToken`, expires) if expires.isAfter(LocalDateTime.now()) =>
        setRefreshedToken(details, token)
        true
      case _ => false
    }

    def setRefreshedToken(details: UserDetailsModel, token: AuthTokenModel): Future[Unit] = {
      val refreshedToken = AuthTokenModel(token.token, LocalDateTime.now().plusMinutes(60))
      setAuthToken(details, refreshedToken)
    }

    mongoConnector.getEntry[UserDetailsModel]("authorisation", "username", Json.toJson(username)).map {
      _.flatMap { details =>
        details.token.map { token =>
          compareToken(details, token)
        }
      }
    }
  }

  def registerUser(username: String, email: String, password: String): Future[Boolean] = {
    val newUser = UserDetailsModel(username, email, encryptionService.encrypt(password))
    val checkUniqueUser: Option[UserDetailsModel] => Boolean = {
      case Some(_) => false
      case _ =>
        mongoConnector.putEntry[UserDetailsModel]("authorisation", newUser)
        true
    }

    mongoConnector.getEntry[UserDetailsModel]("authorisation", "username", Json.toJson(username)).map {
      checkUniqueUser
    }
  }
}
