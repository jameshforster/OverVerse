package models

/**
  * Created by Overlord59 on 14/03/2017.
  */
abstract class Attribute {
  val key: String
  val value: Int
  Validation.validateAttribute(this)
}

case class Volatility(value: Int) extends Attribute {
  val key = "Volatility"
}
