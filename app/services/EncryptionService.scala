package services

import java.nio.charset.StandardCharsets
import java.util.Random

import com.google.inject.{Inject, Singleton}
import config.ApplicationConfig
import org.abstractj.kalium.crypto.SecretBox
import org.abstractj.kalium.encoders.Encoder
import play.api.Logger

/**
  * Created by james-forster on 04/04/17.
  */

@Singleton
class EncryptionService @Inject()(applicationConfig: ApplicationConfig) {

  private val encoder = Encoder.HEX
  private lazy val secretKey = "9aaf832a59bf2f4cf87183c158ced62a1f9b4539015111b4cd47335ad72a1045"

  private val box = {
    new SecretBox(encoder.decode(secretKey))
  }

  def encrypt(input: String): Map[String, String] = {

    def kestrel[A](x: A)(f: A => Unit): A = {
      f(x); x
    }

    val random = new Random()
    val nonce = kestrel(Array.fill[Byte](24)(0))(random.nextBytes)
    val cipherText = box.encrypt(nonce, input.getBytes())
    val nonceHex = encoder.encode(nonce)
    val cipherHex = encoder.encode(cipherText)

    Map("nonce" -> nonceHex, "value" -> cipherHex)
  }

  def decrypt(data: Map[String, String]): String = {
    val nonce = encoder.decode(data("nonce"))
    val cipherText = encoder.decode(data("value"))
    val rawData = box.decrypt(nonce, cipherText)

    new String(rawData, StandardCharsets.UTF_8)
  }
}
