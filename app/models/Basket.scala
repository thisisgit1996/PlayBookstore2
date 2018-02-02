package models

import scala.collection.mutable.ArrayBuffer

case class Basket(var userEmail: String, var books: ArrayBuffer[Book]) {

}
