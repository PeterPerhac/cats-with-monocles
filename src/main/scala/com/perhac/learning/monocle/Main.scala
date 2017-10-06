package com.perhac.learning.monocle

import monocle.{Lens, PLens, Prism}

object Main {

  import monocle.macros.GenLens

  sealed trait Json
  case object JNull extends Json
  case class JStr(v: String) extends Json
  case class JNum(v: Double) extends Json
  case class JObj(v: Map[String, Json]) extends Json

  val companyLens = GenLens[Employee](_.company)
  val addressLens = GenLens[Company](_.address)
  val streetLens = GenLens[Address](_.street)
  val streetNameLens = GenLens[Street](_.name)
  val streetNumberLens = GenLens[Street](_.number)

  private val focusOnStreet = companyLens composeLens addressLens composeLens streetLens

  private def modifier[A, B, C](lens: PLens[A, A, B, B], subFocus: Lens[B, C], f: C => C) = lens composeLens subFocus modify f

  val capitalise = (_: String).split(' ').map(_.capitalize).mkString(" ")
  val increment = (_: Int) + 1

  val fixName = (e: Employee) => e.copy(name = e.name.capitalize)
  val fixStreetName = modifier(focusOnStreet, GenLens[Street](_.name), capitalise)
  val fixStreetNumber = modifier(focusOnStreet, GenLens[Street](_.number), increment)
  val fixEmployee: Employee => Option[Employee] = {
    case Employee(name, _) if "kuko".equalsIgnoreCase(name) => None //we don't like Kuko
    case e => Some(fixStreetName andThen fixStreetNumber andThen fixName apply e)
  }

  def main(args: Array[String]): Unit = {
    val goodStaff = List(
      Employee("john", Company("acme", Address("london", Street(0, "high street")))),
      Employee("paul", Company("acme", Address("london", Street(1, "low avenue")))),
      Employee("tony", Company("acme", Address("london", Street(2, "some kind of place")))),
      Employee("pete", Company("acme", Address("london", Street(3, "shady lane"))))
    )

    val badStaff = List(
      Employee("adam", Company("acme", Address("london", Street(0, "high street")))),
      Employee("jake", Company("acme", Address("london", Street(1, "low avenue")))),
      Employee("neil", Company("acme", Address("london", Street(2, "shady lane")))),
      Employee("kuko", Company("acme", Address("london", Street(3, "some kind of place"))))
    )

    import cats.instances.list._
    import cats.instances.option._
    import cats.syntax.traverse._

    println("group #1")
    goodStaff.traverse(fixEmployee).fold(println("empty"))(_.foreach(println))

    println("===")

    println("group #2")
    //nothing prints, as one of the employees in the list was "fixed" to a None
    //which makes the overall result of the traverse a None
    badStaff.traverse(fixEmployee).fold(println("empty"))(_.foreach(println))








    import monocle.std.double.doubleToInt // Prism[Double, Int] defined in Monocle

    val jNum: Prism[Json, Double] = Prism.partial[Json, Double] { case JNum(v) => v }(JNum)

    val jInt: Prism[Json, Int] = jNum composePrism doubleToInt
    val foo1 = jInt(5)
    println(foo1)

    val foo2 = jInt.getOption(JNum(5.0))
    println(foo2)

    val foo3 = jInt.getOption(JNum(5.2))
    println(foo3)

    val foo4 = jInt.getOption(JStr("Hello"))
    println(foo4)

  }

}

case class Street(number: Int, name: String)

case class Address(city: String, street: Street)

case class Company(name: String, address: Address)

case class Employee(name: String, company: Company)
