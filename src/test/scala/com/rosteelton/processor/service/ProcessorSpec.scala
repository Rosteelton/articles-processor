package com.rosteelton.processor.service

import cats.implicits.toTraverseOps
import com.github.tototoshi.csv.CSVReader
import com.rosteelton.processor.model.{Article, Product}
import com.rosteelton.processor.utils.CsvHelper._
import eu.timepit.refined.types.numeric.NonNegDouble
import eu.timepit.refined.types.string.NonEmptyString
import zio.test._
import java.io.File

object ProcessorSpec extends ZIOSpecDefault {

  val file        = getClass.getClassLoader.getResource("articles.csv")
  val articles = CSVReader.open(new File(file.toURI)).all().traverse(Article.parse).toOption.get

  def product(
      productId: String,
      name: String,
      description: Option[String],
      amount: Double,
      sumOfStocks: Int
  ): Product = {
    Product(NonEmptyString.unsafeFrom(productId), name, description, NonNegDouble.unsafeFrom(amount), sumOfStocks)
  }
  val result = Set(
    product("P-cVBTQHVF", "EDDBZM", Some("fdlViTtefo fpfrnZj hva czIsmugweh"), 8.35, 115),
    product("P-nZZzJFOL", "DBZMGW", Some("dlViTte"), 2.97, 72),
    product("P-NZZzJFOL", "DBZMGW", Some("dlViTte"), 2.97, 69)
  )
  def spec =
    suite("ProcessorSpec")(
      test("should correctly convert articles") {
        assertTrue(ArticleProcessorImpl.convertToProducts(articles).toSet == result)
      }
    )
}
