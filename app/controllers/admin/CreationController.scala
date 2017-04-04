package controllers.admin

import com.google.inject.{Inject, Singleton}
import controllers.OververseController
import models.universe.NewUniverseModel
import play.api.mvc.{Action, AnyContent}
import services.{AdminService, UniverseService}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Overlord59 on 30/03/2017.
  */

@Singleton
class CreationController @Inject()(universeService: UniverseService, adminService: AdminService) extends OververseController {

  val newUniverse: Action[AnyContent] = Action.async { implicit request =>
    boundAction[NewUniverseModel] { model =>
      for {
        universe <- universeService.generateUniverse(model.size)
        save <- adminService.storeUniverse(model.universeName, universe)
        response <- successResponse(save)
      } yield response
    }
  }
}
