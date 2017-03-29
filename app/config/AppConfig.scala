package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration

/**
  * Created by james-forster on 29/03/17.
  */

@Singleton
class ApplicationConfig @Inject()(val configuration: Configuration) extends AppConfig {
  lazy val mongoUrl: String = getUrl("external-connections.database")
}

trait AppConfig {

  val configuration: Configuration

  def getUrl(key: String): String = {
    configuration.getString(key + "protocol").getOrElse("http://") +
    configuration.getString(key + "domain").get
    configuration.getString(key + "port").get
    configuration.getString(key + "context").getOrElse("")
  }

}
