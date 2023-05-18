package com.rosteelton.processor.service

import cats.implicits.toTraverseOps
import com.rosteelton.processor.model.{Article, Product}
import com.rosteelton.processor.utils.{AppError, CsvHelper}
import eu.timepit.refined.types.numeric.NonNegBigDecimal
import eu.timepit.refined.types.string.NonEmptyString
import zio.stream.{ZSink, ZStream}
import zio.test._

import scala.io.Source

object ProcessorSpec extends ZIOSpecDefault {

  val articles = ZStream
    .from(
      Source
        .fromResource("articles.csv")
        .getLines()
    )
    .map(_.split(CsvHelper.delimiter).toList)
    .map(line => Article.parse(line).toOption)
    .collectSome
    .orElseFail(AppError.ShopApiError("ERR"))

  def product(
      productId: String,
      name: String,
      description: Option[String],
      amount: Double,
      sumOfStocks: Int
  ): Product = {
    Product(NonEmptyString.unsafeFrom(productId), name, description, NonNegBigDecimal.unsafeFrom(amount), sumOfStocks)
  }
  val result = List(
    product("P-cVBTQHVF", "EDDBZM", Some("fdlViTtefo fpfrnZj hva czIsmugweh"), 8.35, 115),
    product("P-nZZzJFOL", "DBZMGW", Some("dlViTte"), 2.97, 72),
    product("P-NZZzJFOL", "DBZMGW", Some("dlViTte"), 2.97, 69)
  )

  def spec =
    suite("ProcessorSpec")(
      test("should correctly convert articles") {
        ArticleProcessorImpl.convertArticles(articles).run(ZSink.collectAll).map(r => assertTrue(r.toList == result))
      }
    )
}
