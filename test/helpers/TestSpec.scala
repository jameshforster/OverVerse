package helpers

import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by james-forster on 14/03/17.
  */
trait TestSpec extends WordSpec with Matchers with MockitoSugar {

  def await[T](future: Future[T]): T = {
    Await.result(future, 5 seconds)
  }
}
