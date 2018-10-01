package com.perhac.learning.monocle.lensesandprisms

import monocle.Prism
import monocle.function.Each._
import monocle.macros.{GenPrism, Lenses}

sealed trait Postcode

object Postcode {
  val ukPostcode: Prism[Postcode, UkPostcode] = GenPrism[Postcode, UkPostcode]
  val slovakPostcode: Prism[Postcode, SlovakPostcode] = GenPrism[Postcode, SlovakPostcode]
}

sealed trait Country

case object UnitedKingdom extends Country

case object Slovakia extends Country

@Lenses
case class UkPostcode(outward: String, inward: String) extends Postcode {
  override def toString: String = s"$outward $inward"
}

@Lenses
case class SlovakPostcode(number: String) extends Postcode

@Lenses
case class Address(line1: String, line2: Option[String], town: String, postcode: Postcode, country: Country)

@Lenses
case class Person(name: String, age: Int, addresses: List[Address])

object LensesAndPrisms {

  import Address._
  import Person._
  import Postcode._
  import SlovakPostcode._
  import UkPostcode._

  val puk = Person("Peter Perhac", 33, addresses = List(
    Address("1 Doe Court", Some("Pretty Crescent"), "Hove", UkPostcode("BN3", "5AB"), UnitedKingdom),
    Address("2 Smith Road", None, "Brighton", UkPostcode("BN1", "1AB"), UnitedKingdom)
  ))

  val psk = Person("Peter Perhac", 33, addresses = List(
    Address("Duklianska 4", None, "Spisska Nove Ves", SlovakPostcode("052 01"), Slovakia),
    Address("ul. Narodneho Odboja 8", None, "Poprad", SlovakPostcode("058 01"), Slovakia)
  ))

  def main(args: Array[String]): Unit = {

    val ukOutwardPostcode = addresses ^|->> each ^|-> postcode ^<-? ukPostcode ^|-> outward
    val skPostCode = addresses ^|->> each ^|-> postcode ^<-? slovakPostcode ^|-> number

    println(ukOutwardPostcode.getAll(puk))
    println(ukOutwardPostcode.modify(_.reverse)(puk))
    println(ukOutwardPostcode.getAll(psk))
    println(ukOutwardPostcode.modify(_.reverse)(psk))

    println(skPostCode.getAll(puk))
    println(skPostCode.modify(_.reverse)(puk))
    println(skPostCode.getAll(psk))
    println(skPostCode.modify(_.reverse)(psk))
  }

}
