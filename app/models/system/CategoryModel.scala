package models.system

import play.api.libs.json._

/**
  * Created by Overlord59 on 25/03/2017.
  */
case class CategoryModel (name: String, conditions: Seq[StarModel => Boolean])

object CategoryModel {
  def apply(name: String): CategoryModel = {
    allCategories.find(_.name == name).getOrElse(throw new Exception("Invalid star category provided."))
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

  def hasAge(max: Int, min: Int): StarModel => Boolean = { star =>
    star.age >= min && star.age <= max
  }

  val redDwarf = CategoryModel("Red Dwarf", Seq(hasSize(max = 1, min = 1)))
  val yellowStar = CategoryModel("Yellow Star", Seq(hasSize(max = 4, min = 2), hasAge(max = 3, min = 1)))
  val redGiant = CategoryModel("Red Giant", Seq(hasSize(max = 4, min = 2), hasAge(4, 4)))
  val whiteDwarf = CategoryModel("White Dwarf", Seq(hasSize(max = 4, min = 2), hasAge(5, 5)))
  val whiteStar = CategoryModel("White Star", Seq(hasSize(max = 6, min = 5), hasAge(max = 2, min = 1)))
  val blueStar = CategoryModel("Blue Star", Seq(hasSize(max = 6, min = 5), hasAge(max = 2, min = 1)))
  val blueGiant = CategoryModel("Blue Giant", Seq(hasSize(max = 6, min = 5), hasAge(max = 5, min = 3)))

  val allCategories = Seq(redDwarf, yellowStar, redGiant, whiteDwarf, whiteStar, blueStar, blueGiant)
}
