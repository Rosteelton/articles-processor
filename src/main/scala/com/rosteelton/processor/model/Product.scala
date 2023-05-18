package com.rosteelton.processor.model

import com.rosteelton.processor.utils.CsvHelper
import eu.timepit.refined.types.numeric.NonNegBigDecimal
import eu.timepit.refined.types.string.NonEmptyString

case class Product(
    productId: NonEmptyString,
    name: String,
    description: Option[String],
    amount: NonNegBigDecimal,
    sumOfStocks: Int
) {
  def toCsvLine: String =
    List(productId.value, name, description.getOrElse(""), amount.value.setScale(2).toString(), sumOfStocks.toString)
      .mkString(CsvHelper.delimiter.toString)
}

object Product {
  def fromArticle(a: Article): Product =
    Product(a.productId, a.name, a.description, a.amount, a.stock.value)

  val header: String = "produktId|name|beschreibung|preis|summeBestand"
}
