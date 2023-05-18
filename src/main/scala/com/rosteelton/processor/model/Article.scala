package com.rosteelton.processor.model

import cats.implicits.toBifunctorOps
import eu.timepit.refined.types.all.NonEmptyString
import eu.timepit.refined.types.numeric.{NonNegBigDecimal, NonNegInt}

import scala.util.Try

case class Article(
    id: NonEmptyString,
    productId: NonEmptyString,
    name: String,
    description: Option[String],
    amount: NonNegBigDecimal,
    stock: NonNegInt
)

object Article {
  def parse(input: List[String]): Either[String, Article] =
    input match {
      case List(id, productId, name, description, amount, stock) =>
        for {
          id        <- NonEmptyString.from(id)
          productId <- NonEmptyString.from(productId)
          amount <-
            Try(BigDecimal(amount)).toEither
              .leftMap(_ => s"Wrong amount format: ${amount}")
              .flatMap(NonNegBigDecimal.from)
          stock <- stock.toIntOption.toRight(s"Wrong stock format: $stock").flatMap(NonNegInt.from)
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
