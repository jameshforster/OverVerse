package services.auth

import java.time.{LocalDate, LocalDateTime}

import connectors.MongoConnector
import helpers.TestSpec
import models.auth.{AuthTokenModel, UserDetailsModel}
import org.mockito.ArgumentMatchers
import org.scalatestplus.play.OneAppPerSuite
import org.mockito.Mockito._

import scala.concurrent.Future

/**
  * Created by Overlord59 on 06/04/2017.
  */
class AuthorisationServiceSpec extends TestSpec with OneAppPerSuite {

  lazy val encryptionService: EncryptionService = app.injector.instanceOf[EncryptionService]

  def setupService(foundUsers: Future[Option[UserDetailsModel]]): AuthorisationService = {
    val mockConnector = mock[MongoConnector]

    when(mockConnector.getEntry[UserDetailsModel](ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(foundUsers)

    when(mockConnector.updateEntry[UserDetailsModel](ArgumentMatchers.any(), ArgumentMatchers.any(),
      ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful{})

    new AuthorisationService(encryptionService, mockConnector)
  }

  "Calling .loginUser" when {

    "no matching user is found" should {
      lazy val service = setupService(Future.successful(None))
      lazy val result = service.loginUser("name", "testData")

      "return a None" in {
        await(result) shouldBe None
      }
    }

    "a matching user is found with the wrong password" should {
      val map = Map(
        "nonce" -> "a954bf74662060335285a4b482055ef8b9b38eeee1808f97ea7602fcde77b2ed",
        "value" -> "33b8c73001f82ca28f3e26e1af1db245"
      )
      val user = UserDetailsModel("name", "name@example.com", map)
      lazy val service = setupService(Future.successful(Some(user)))
      lazy val result = service.loginUser("name", "wrongPassword")

      "return a None" in {
        await(result) shouldBe None
      }
    }

    "a matching user is found with the correct password" should {
      val map = Map(
        "nonce" -> "a954bf74662060335285a4b482055ef8b9b38eeee1808f97ea7602fcde77b2ed",
        "value" -> "33b8c73001f82ca28f3e26e1af1db245"
      )
      val user = UserDetailsModel("name", "name@example.com", map)
      lazy val service = setupService(Future.successful(Some(user)))
      lazy val result = service.loginUser("name", "testData")

      "return a None" in {
        await(result).isDefined shouldBe true
      }
    }

    "an error occurs in the connector" should {
      lazy val service = setupService(Future.failed(new Exception("error message")))
      lazy val result = service.loginUser("name", "testData")

      "return the correct exception" in {
        val exception = intercept[Exception] {await(result)}

        exception.getMessage shouldBe "error message"
      }
    }
  }

  "Calling .validateUser" when {

    "no matching user is found" should {
      lazy val service = setupService(Future.successful(None))
      lazy val result = service.validateUser("name", "testData")

      "return a None" in {
        await(result) shouldBe None
      }
    }

    "a matching user is found with the wrong token" should {
      val map = Map(
        "nonce" -> "a954bf74662060335285a4b482055ef8b9b38eeee1808f97ea7602fcde77b2ed",
        "value" -> "33b8c73001f82ca28f3e26e1af1db245"
      )
      val token = "33b8c73001f82ca28f3e26e1af1db245"
      val user = UserDetailsModel("name", "name@example.com", map, token = Some(AuthTokenModel(token, LocalDateTime.now().plusMinutes(5))))
      lazy val service = setupService(Future.successful(Some(user)))
      lazy val result = service.validateUser("name", "wrong token")

      "return a false" in {
        await(result) shouldBe Some(false)
      }
    }

    "a matching user is found with an outdated token" should {
      val map = Map(
        "nonce" -> "a954bf74662060335285a4b482055ef8b9b38eeee1808f97ea7602fcde77b2ed",
        "value" -> "33b8c73001f82ca28f3e26e1af1db245"
      )
      val token = "33b8c73001f82ca28f3e26e1af1db245"
      val user = UserDetailsModel("name", "name@example.com", map, token = Some(AuthTokenModel(token, LocalDateTime.now().minusMinutes(5))))
      lazy val service = setupService(Future.successful(Some(user)))
      lazy val result = service.validateUser("name", "33b8c73001f82ca28f3e26e1af1db245")

      "return a true" in {
        await(result) shouldBe Some(false)
      }
    }

    "a matching user is found with the correct token" should {
      val map = Map(
        "nonce" -> "a954bf74662060335285a4b482055ef8b9b38eeee1808f97ea7602fcde77b2ed",
        "value" -> "33b8c73001f82ca28f3e26e1af1db245"
      )
      val token = "33b8c73001f82ca28f3e26e1af1db245"
      val user = UserDetailsModel("name", "name@example.com", map, token = Some(AuthTokenModel(token, LocalDateTime.now().plusMinutes(5))))
      lazy val service = setupService(Future.successful(Some(user)))
      lazy val result = service.validateUser("name", "33b8c73001f82ca28f3e26e1af1db245")

      "return a true" in {
        await(result) shouldBe Some(true)
      }
    }

    "an error occurs in the connector" should {
      lazy val service = setupService(Future.failed(new Exception("error message")))
      lazy val result = service.validateUser("name", "testData")

      "return the correct exception" in {
        val exception = intercept[Exception] {await(result)}

        exception.getMessage shouldBe "error message"
      }
    }
  }
}
