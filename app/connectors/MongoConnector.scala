package connectors

import com.google.inject.{Inject, Singleton}
import config.ApplicationConfig
import play.api.Logger
import play.api.libs.json._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by james-forster on 29/03/17.
  */

@Singleton
class MongoConnector @Inject()(applicationConfig: ApplicationConfig, val reactiveMongoApi: ReactiveMongoApi) extends ReactiveMongoComponents {

  def collection(collectionName: String): Future[JSONCollection] = reactiveMongoApi.database.map { database => database.collection(collectionName) }

  def getEntry[T](collectionName: String, key: String, value: JsObject)(implicit format: OFormat[T]): Future[T] = {

    val getCollection = collection(collectionName)
    val handler: Cursor.ErrorHandler[List[T]] = {
      (last: List[T], error: Throwable) =>
        Logger.warn(s"Error encountered fetching entry: ${error.getMessage}")

        if (last.isEmpty) {
          Cursor.Cont(last)
        } else Cursor.Fail(error)
    }

    def filterList(collection: JSONCollection): Future[List[T]] = {
      collection.find(Json.obj(key -> value)).cursor[T]().collect[List](maxDocs = -1, err = handler)
    }

    for {
      collection <- getCollection
      list <- filterList(collection)
    } yield list.head
  }

  def putEntry[T](collectionName: String, document: T)(implicit writes: OWrites[T]): Future[Unit] = {

    val getCollection = collection(collectionName)

    def insertDocument(collection: JSONCollection): Future[WriteResult] = {
      collection.insert(document)
    }

    def mapResult(result: WriteResult): Future[Unit] = {
      if (result.ok) Future.successful {}
      else throw new Exception(s"Failed to write to database: ${result.code.get} ${result.writeErrors}")
    }

    for {
      collection <- getCollection
      insert <- insertDocument(collection)
      result <- mapResult(insert)
    } yield result
  }
}
