package controllers

import javax.inject.Inject

import models.{Basket, Book, Customer}
import models.Customer._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.mvc._

import scala.collection.mutable.ArrayBuffer

class Application @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  var loginEmail = ""

  val books = ArrayBuffer[Book](
    Book(0, "Fire and Fury: Inside the Trump White House (Hardback)", "Michael Wolff"),
    Book(1, "Eleanor Oliphant is Completely Fine", "Gail Honeyman"),
    Book(2, "Why We Sleep", "Matthew Walker"),
    Book(3, "Still Me", "Jojo Moyes"),
    Book(4, "Lose Weight For Good", "Andy Mooney"),
    Book(5, "Sirens", "Joseph Knox"),
    Book(6, "Bernard MacLaverty Midwinter Break", "Anne Enright"),
    Book(7, "12 Rules For Life", "Jordan B Peterson"),
    Book(8, "Lee Child No Middle Name", "Jack Reacher")

  )

  val baskets = ArrayBuffer[Basket](
    Basket("leo@gmail.com", books),
    Basket("jordan@gmail.com", ArrayBuffer[Book](Book(4, "Lose Weight For Good", "Andy Mooney"), Book(4, "Lose Weight For Good", "Andy Mooney"))),
    Basket("andy@gmail.com", ArrayBuffer[Book](Book(4, "Lose Weight For Good", "Andy Mooney"), Book(7, "12 Rules For Life", "Jordan B Peterson")))

  )

  val customers = ArrayBuffer[Customer](
    Customer("leo@gmail.com", "lpass"),
    Customer("jordan@gmail.com", "jpass"),
    Customer("andy@gmail.com", "apass")
  )

  def index = Action {
    Ok(views.html.index(loginEmail))
  }

  def storeFinder = Action {
    Ok(views.html.storeFinder(loginEmail))
  }

  def navbar = Action {
    Ok(views.html.navbar(loginEmail))
  }

  def signInUp = Action {
    Ok(views.html.signInUp(Customer.createCustomerForm, loginEmail))
  }

  def signUp = Action { implicit request: Request[AnyContent] =>
    Customer.createCustomerForm.bindFromRequest.fold(
      { formWithErrors =>
        BadRequest(views.html.signInUp(formWithErrors, loginEmail))
      },
      { aCustomer =>
        if (existsEmail(aCustomer)) {
          if (customers.filter(_.email.equals((aCustomer.email))).head.password.equals(aCustomer.password)) {
            loginEmail = aCustomer.email
            Ok(views.html.loginSuccess(loginEmail))
          } else {
            BadRequest(views.html.signInUp(Customer.createCustomerForm, loginEmail))
          }
        } else {
          addCustomer(aCustomer)
          addBasket(aCustomer)
          loginEmail = aCustomer.email
          Ok(views.html.loginSuccess(loginEmail))
        }


      })
  }

  def existsEmail(customer: Customer): Boolean = {
    if (customers.filter(_.email.equals(customer.email)).isEmpty) {
      false
    } else true
  }

  def addCustomer(customer: Customer): Unit = {
    if (!customers.contains(customer)) {
      customers.append(customer)
    }

  }

  def addBasket(customer: Customer): Unit = {
    if (baskets.filter(_.userEmail.equals(customer.email)).isEmpty) {
      baskets.append(Basket(customer.email, ArrayBuffer[Book]()))
    }
  }

  def carouselBS = Action {
    Ok(views.html.carouselBestSellers(loginEmail))
  }

  def trump = Action {
    Ok(views.html.trump(loginEmail))
  }

  def logOut = Action {
    loginEmail = ""
    Ok(views.html.index(loginEmail))
  }

  def bookPage(idIn: Int) = Action {
    var bookIn = books.filter(_.id.equals(idIn)).head
    Ok(views.html.bookPage(loginEmail, bookIn))
  }

  def basket() = Action {
    Ok(views.html.basket(loginEmail, findBasketByEmail(loginEmail)))
  }

  def findBasketByEmail(email: String): Basket = {
    baskets.filter(_.userEmail.equals(email)).head
  }

  def findBookByID(inID: Int): Book = {
    books.filter(_.id.equals(inID)).head
  }

  def addToBasket(idIn: Int) = Action {
    var bskt = findBasketByEmail(loginEmail)
    bskt.books.append(findBookByID(idIn))
    Ok(views.html.basket(loginEmail, bskt))
  }

  def deleteFromBasket(idIn: Int) = Action {
    var bskt = findBasketByEmail(loginEmail)
    bskt.books.remove(idIn)
    Ok(views.html.basket(loginEmail, bskt))
  }


}