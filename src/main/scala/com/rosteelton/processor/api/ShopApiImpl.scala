package com.rosteelton.processor.api

import cats.implicits.toTraverseOps
import com.github.tototoshi.csv.CSVReader
import com.rosteelton.processor.api.ShopApi.ShopApiError
import com.rosteelton.processor.config.{AppConfig, HttpClientConfig}
import com.rosteelton.processor.model.Article
import sttp.client3._
import zio._
import com.rosteelton.processor.utils.CsvHelper._

import java.io.File

class ShopApiImpl(sttpBackend: SttpBackend[Task, Any], config: HttpClientConfig) extends ShopApi {
  def getArticles(limit: Int): IO[ShopApiError, List[Article]] = {

    val req = basicRequest
      .get(uri"${config.baseUrl}/articles/$limit")
      .response(asFile(new File("articles.csv")))

    for {
      response <- sttpBackend.send(req).mapError(th => ShopApiError.SttpError(th.getMessage))
      file <- response.body match {
        case Left(value)  => ZIO.fail(ShopApiError.SttpError(value))
        case Right(value) => ZIO.succeed(value)
      }

      result <-
        ZIO
          .fromEither(CSVReader.open(file).all().drop(1).traverse(Article.parse))
          .mapError(ShopApiError.ParsingArticleError)
    } yield result
  }
}

object ShopApiImpl {
  val live: ZLayer[AppConfig with SttpBackend[Task, Any], Nothing, ShopApi] =
    ZLayer {
      for {
        client <- ZIO.service[SttpBackend[Task, Any]]
        config <- ZIO.service[AppConfig]
      } yield new ShopApiImpl(client, config.shopApi)
    }
}
