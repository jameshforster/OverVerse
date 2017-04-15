package controllers

import helpers.TestSpec
import models.auth.{LoginModel, NewUserModel}
import models.exceptions.{DuplicateUserException, IncorrectPasswordException, UserNotFoundException}
import org.mockito.ArgumentMatchers
import services.auth.AuthorisationService
import org.mockito.Mockito._
import play.api.libs.json.Json
import play.api.test.FakeRequest

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by james-forster on 15/04/17.
  */
class UserControllerSpec extends TestSpec {

  def setupController(response: Future[Unit] = Future.successful{},
                     token: Future[String] = Future.successful("")): UserController = {
    val mockAuthorisationService = mock[AuthorisationService]

    when(mockAuthorisationService.registerUser(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
    .thenReturn(response)

    when(mockAuthorisationService.loginUser(ArgumentMatchers.any(), ArgumentMatchers.any()))
    .thenReturn(token)

    new UserController(mockAuthorisationService)
  }

  "Calling .createUser" when {

    "an empty json body is passed" should {
      lazy val controller = setupController()
      lazy val result = controller.createUser(FakeRequest("POST", ""))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "return the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "an invalid json body is passed" should {
      lazy val controller = setupController()
      lazy val result = controller.createUser(FakeRequest("POST", "").withJsonBody(Json.toJson("")))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "return the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "a matching user already exists" should {
      lazy val controller = setupController(response = Future {throw new DuplicateUserException})
      lazy val result = controller.createUser(FakeRequest("POST", "").withJsonBody(Json.toJson(NewUserModel("test", "", ""))))

      "return a status of 409" in {
        statusOf(result) shouldBe 409
      }

      "return the correct error message" in {
        bodyOf(result) should include("A user with the username: test already exists.")
      }
    }

    "an unexpected error occurs" should {
      lazy val controller = setupController(response = Future.failed(new Exception("test message")))
      lazy val result = controller.createUser(FakeRequest("POST", "").withJsonBody(Json.toJson(NewUserModel("test", "", ""))))

      "return a status of 500" in {
        statusOf(result) shouldBe 500
      }

      "return the correct error message" in {
        bodyOf(result) should include("test message")
      }
    }

    "a new user is created" should {
      lazy val controller = setupController()
      lazy val result = controller.createUser(FakeRequest("POST", "").withJsonBody(Json.toJson(NewUserModel("test", "", ""))))

      "return a status of 204" in {
        statusOf(result) shouldBe 204
      }
    }
  }

  "Calling .loginUser" when {

    "an empty json body is passed" should {
      lazy val controller = setupController()
      lazy val result = controller.loginUser(FakeRequest("POST", ""))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "return the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "an invalid json body is passed" should {
      lazy val controller = setupController()
      lazy val result = controller.loginUser(FakeRequest("POST", "").withJsonBody(Json.toJson("")))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "return the correct error message" in {
        bodyOf(result) should include("Could not bind request body to json due to:")
      }
    }

    "a matching user is not found" should {
      lazy val controller = setupController(token = Future {throw new UserNotFoundException})
      lazy val result = controller.loginUser(FakeRequest("POST", "").withJsonBody(Json.toJson(LoginModel("test", ""))))

      "return a status of 404" in {
        statusOf(result) shouldBe 404
      }

      "return the correct error message" in {
        bodyOf(result) should include("Resource: test does not exist.")
      }
    }

    "an incorrect password is provided" should {
      lazy val controller = setupController(token = Future {throw new IncorrectPasswordException})
      lazy val result = controller.loginUser(FakeRequest("POST", "").withJsonBody(Json.toJson(LoginModel("test", ""))))

      "return a status of 400" in {
        statusOf(result) shouldBe 400
      }

      "return the correct error message" in {
        bodyOf(result) should include("password request does not match existing records")
      }
    }

    "an unexpected error occurs" should {
      lazy val controller = setupController(token = Future.failed(new Exception("error message")))
      lazy val result = controller.loginUser(FakeRequest("POST", "").withJsonBody(Json.toJson(LoginModel("test", ""))))

      "return a status of 500" in {
        statusOf(result) shouldBe 500
      }

      "return the correct error message" in {
        bodyOf(result) should include("Unexpected error occurred: error message")
      }
    }

    "correct login details are provided" should {
      lazy val controller = setupController()
      lazy val result = controller.loginUser(FakeRequest("POST", "").withJsonBody(Json.toJson(LoginModel("test", ""))))

      "return a status of 200" in {
        statusOf(result) shouldBe 200
      }

      "return a body containing the generated token" in {
        bodyOf(result) shouldBe """{"username":"test","token":""}"""
      }
    }
  }
}
