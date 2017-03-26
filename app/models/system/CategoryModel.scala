package models.system

import play.api.libs.json._

/**
  * Created by Overlord59 on 25/03/2017.
  */
case class CategoryModel (name: String, conditions: Seq[StarModel => Boolean])

object CategoryModel {
  def apply(name: String): CategoryModel = {
    allCategories.find(_.name == name).getOrElse(star)
  }

  implicit val formatter: OFormat[CategoryModel] = new OFormat[CategoryModel] {
    override def writes(o: CategoryModel): JsObject = Json.obj("name" -> o.name)

    override def reads(json: JsValue): JsResult[CategoryModel] = {
      val name = (json \ "name").validate[String]
      name.map(name => CategoryModel(name))
    }
  }

  def hasSize(max: Int, min: Int): StarModel => Boolean = { star =>
    star.size >= min && star.size <= max
  }

  val star = CategoryModel("Star", Seq(hasSize(max = 6, min = 3)))
  val giant = CategoryModel("Giant", Seq(hasSize(max = 10, min = 7)))
  val dwarf = CategoryModel("Dwarf", Seq(hasSize(max = 2, min = 1)))
  val allCategories = Seq(star, giant, dwarf)
}
