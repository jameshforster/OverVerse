package controllers

import com.google.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent}
import services.MapService

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by james-forster on 03/04/17.
  */

@Singleton
class MapController @Inject()(mapService: MapService) extends OververseController {

  val universeMap: Action[AnyContent] = boundAction[String] { name =>
    mapService.getUniverse(name).flatMap {
      case universe if universe.sectors.isEmpty => notFound(name)
      case universe => okResponse(universe)
    }
  }
}
