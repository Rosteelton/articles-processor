package com.rosteelton.processor.model

import com.github.tototoshi.csv.CSVFormat
import eu.timepit.refined.types.numeric.NonNegDouble
import eu.timepit.refined.types.string.NonEmptyString

case class Product(
    productId: NonEmptyString,
    name: String,
    description: Option[String],
    amount: NonNegDouble,
    sumOfStocks: Int
) {
  def toCsvLine(implicit format: CSVFormat): String =
    List(productId.value, name, description.getOrElse(""), amount.value.toString, sumOfStocks.toString)
      .mkString(format.delimiter.toString)
}

object Product {
  def fromArticle(a: Article): Product =
    Product(a.productId, a.name, a.description, a.amount, a.stock.value)

  val header: String = "produktId|name|beschreibung|preis|summeBestand"
}
