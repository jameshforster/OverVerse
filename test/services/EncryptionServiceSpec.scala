package services

import config.ApplicationConfig
import helpers.TestSpec
import org.scalatestplus.play.OneAppPerSuite

/**
  * Created by james-forster on 04/04/17.
  */
class EncryptionServiceSpec extends TestSpec with OneAppPerSuite {

  lazy val config = app.injector.instanceOf[ApplicationConfig]
  lazy val service = new EncryptionService(config)

  "Calling .encrypt" should {
    lazy val result = service.encrypt("testData")

    "return a map" which {

      "contains a nonce of size 48" in {
        result("nonce").length shouldBe 48
      }

      "contains a value of size 48" in {
        result("value").length shouldBe 48
      }
    }
  }

  "Calling .decrypt" should {
    val map = Map("nonce" -> "97c4dfbab036962a81b2cecc1f23806bf0e756ade9ebbd51", "value" -> "9785515b65db47a7e41aea06b662298152740c5987bba977")
    lazy val result = service.decrypt(map)

    "return a string of 'testData'" in {
      result shouldBe "testData"
    }
  }
}
