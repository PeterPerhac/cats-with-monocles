package com.perhac.learning.monocle

case class Street(number: Int, name: String)

case class Address(city: String, street: Street)

case class Company(name: String, address: Address)

case class Employee(name: String, company: Company)

object Main {

  import monocle.Lens
  import monocle.macros.GenLens

  val company: Lens[Employee, Company] = GenLens[Employee](_.company)
  val address: Lens[Company, Address] = GenLens[Company](_.address)
  val street: Lens[Address, Street] = GenLens[Address](_.street)
  val streetName: Lens[Street, String] = GenLens[Street](_.name)


  val capitaliseEachWordInString: String => String = _.split(' ').map(_.capitalize).mkString(" ")

  def main(args: Array[String]): Unit = {
    val employee1 = Employee("john", Company("awesome inc", Address("london", Street(23, "high street"))))
    val employee2 = Employee("paul", Company("awesome inc", Address("london", Street(23, "low avenue"))))

    val fixStreetName = company composeLens address composeLens street composeLens streetName modify capitaliseEachWordInString

    List(employee1, employee2).map(fixStreetName).foreach(println)

  }

}
