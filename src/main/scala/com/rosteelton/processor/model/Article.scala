package com.rosteelton.processor.model

import eu.timepit.refined.types.all.NonEmptyString
import eu.timepit.refined.types.numeric.{NonNegDouble, NonNegInt}

case class Article(
    id: NonEmptyString,
    productId: NonEmptyString,
    name: String,
    description: Option[String],
    amount: NonNegDouble,
    stock: NonNegInt
)

object Article {
  def parse(input: List[String]): Either[String, Article] =
    input match {
      case List(id, productId, name, description, amount, stock) =>
        for {
          id        <- NonEmptyString.from(id)
          productId <- NonEmptyString.from(productId)
          amount    <- amount.toDoubleOption.toRight(s"Wrong amount format: ${amount}").flatMap(NonNegDouble.from)
          stock     <- stock.toIntOption.toRight(s"Wrong stock format: $stock").flatMap(NonNegInt.from)
        } yield Article(
          id,
          productId,
          name,
          if (description.isEmpty) None else Some(description),
          amount,
          stock
        )
      case list => Left[String, Article](s"Wrong list: ${list.mkString}")
    }
}
