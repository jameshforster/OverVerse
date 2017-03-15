package models

/**
  * Created by Overlord59 on 14/03/2017.
  */
object Validation {
  def validateAttribute(attribute: Attribute): Unit = {

    def validateAttributeValue(value: Int): Boolean = {
      value >= 0 && value <= 5
    }

    require(validateAttributeValue(attribute.value), s"The value ${attribute.value} is not valid for an attribute.")
  }

  def validateSize(size: Int): Unit = {

    def validateSizeValue(value: Int): Boolean = {
      value >= 1 && value <= 9
    }

    require(validateSizeValue(size), s"A size of $size is invalid for a planet.")
  }
}