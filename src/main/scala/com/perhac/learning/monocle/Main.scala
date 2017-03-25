package com.perhac.learning.monocle

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

case class Street(number: Int, name: String)

case class Address(city: String, street: Street)

case class Company(name: String, address: Address)

case class Employee(name: String, company: Company)

object Main {

  import cats.instances.future._
  import cats.instances.list._
  import cats.syntax.traverse._
  import monocle.Lens
  import monocle.macros.GenLens

  val company: Lens[Employee, Company] = GenLens[Employee](_.company)
  val address: Lens[Company, Address] = GenLens[Company](_.address)
  val street: Lens[Address, Street] = GenLens[Address](_.street)
  val streetName: Lens[Street, String] = GenLens[Street](_.name)

  val capitaliseEachWordInString: String => String = _.split(' ').map(_.capitalize).mkString(" ")
  val fixStreetName = company composeLens address composeLens street composeLens streetName modify capitaliseEachWordInString
  val capitaliseStreetName = (e: Employee) => Future(fixStreetName(e))

  def main(args: Array[String]): Unit = {
    val employees = List(
      Employee("john", Company("awesome inc", Address("london", Street(1, "high street")))),
      Employee("paul", Company("awesome inc", Address("london", Street(2, "low avenue")))),
      Employee("tony", Company("awesome inc", Address("london", Street(3, "some kind of place")))),
      Employee("pete", Company("awesome inc", Address("london", Street(4, "shady lane"))))
    )

    Await.result(employees.traverse(capitaliseStreetName).map(_.foreach(println)), 1 second)
  }

}
