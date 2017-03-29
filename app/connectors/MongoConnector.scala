package connectors

import com.google.inject.{Inject, Singleton}
import config.ApplicationConfig
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{Document, MongoClient}
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by james-forster on 29/03/17.
  */

@Singleton
class MongoConnector @Inject()(applicationConfig: ApplicationConfig) {

  implicit val documentToJsValue: Document => JsValue = { document =>
    val map = document.toMap

    Json.obj()
  }

  val mongoClient = MongoClient(applicationConfig.mongoUrl)

  val database = mongoClient.getDatabase("overVerse")

  def collection(name: String) = database.getCollection(name)

  def getDocument[T](collectionName: String, document: String): Future[JsValue] = {

    collection(collectionName).find(equal("", "")).first().head().map { document =>
      document
    }
  }
}
