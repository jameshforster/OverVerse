package services.auth

import java.time.LocalDateTime

import com.google.inject.{Inject, Singleton}
import connectors.MongoConnector
import models.auth.{AuthTokenModel, UserDetailsModel}
import org.apache.commons.codec.binary.Hex
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Overlord59 on 05/04/2017.
  */

@Singleton
class AuthorisationService @Inject()(encryptionService: EncryptionService, mongoConnector: MongoConnector) {

  def createToken(): AuthTokenModel = {
    val encoder = new Hex
    val token = new String(encoder.encode(Random.nextString(8).getBytes()))
    AuthTokenModel(encryptionService.encrypt(token), LocalDateTime.now().plusMinutes(30))
  }

  def loginUser(username: String, password: String): Future[Option[String]] = {
    mongoConnector.getEntry[UserDetailsModel]("authorisation", "username", Json.toJson(username)).map {
     _.flatMap { details =>
        encryptionService.decrypt(details.password) match {
          case `password` =>
            val token = createToken()
            val user = UserDetailsModel(details.username, details.email, details.password, details.isActivated, Some(token))

            mongoConnector.updateEntry[UserDetailsModel]("authorisation", "username", Json.toJson(username), user)
            Some(token.token("value"))

          case _ => None
        }
      }
    }
  }

  def validateUser(username: String, authToken: String): Future[Option[Boolean]] = {
    mongoConnector.getEntry[UserDetailsModel]("authorisation", "username", Json.toJson(username)).map {
      _.flatMap { details =>
        details.token.map { token =>
          if(token.expiresOn.isAfter(LocalDateTime.now())) {
            val matching = authToken == token.token("value")
            if (matching) {
              val refreshedToken = AuthTokenModel(token.token, LocalDateTime.now().plusMinutes(30))
              val refreshedUser = UserDetailsModel(details.username, details.email, details.password, details.isActivated, Some(refreshedToken))
              mongoConnector.updateEntry[UserDetailsModel]("authorisation", "username", Json.toJson(username), refreshedUser)
            }
            matching
          } else false
        }
      }
    }
  }
}
