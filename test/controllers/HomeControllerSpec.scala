package controllers

import helpers.TestSpec
import org.scalatestplus.play.OneAppPerSuite
import play.api.test.FakeRequest

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by Overlord59 on 15/03/2017.
  */
class HomeControllerSpec extends TestSpec with OneAppPerSuite {

  "Calling .index" should {
    lazy val controller = new HomeController
    lazy val result = Await.result(controller.index(FakeRequest("GET", "")), 5 second)

    "return a status of 200" in {
      result.header.status shouldBe 200
    }
  }
}
