package com.rosteelton.processor.api

import com.rosteelton.processor.config.{AppConfig, HttpClientConfig}
import com.rosteelton.processor.model.Article
import com.rosteelton.processor.utils.{AppError, CsvHelper}
import sttp.capabilities.zio.ZioStreams
import sttp.client3._
import zio._
import zio.stream.{ZPipeline, ZStream}

class ShopApiImpl(sttpBackend: SttpBackend[Task, ZioStreams], config: HttpClientConfig) extends ShopApi {
  def getArticles(limit: Int): ZStream[Any, AppError, Article] = {

    val req = basicRequest
      .get(uri"${config.baseUrl}/articles/$limit")
      .response(asStreamUnsafe(ZioStreams))

    ZStream.unwrap {
      for {
        response <- sttpBackend.send(req).mapError(th => AppError.ShopApiError(th.getMessage))
        csv <- response.body match {
          case Left(value)  => ZIO.fail(AppError.ShopApiError(value))
          case Right(value) => ZIO.succeed(value)
        }
      } yield csv
        .via(ZPipeline.utf8Decode)
        .via(ZPipeline.splitLines)
        .drop(1)
        .mapBoth(e => AppError.ParsingError(e.getMessage), CsvHelper.splitLine)
        .mapZIO(line =>
          ZIO
            .fromEither(Article.parse(line))
            .mapError(AppError.ParsingError)
        )
    }
  }
}

object ShopApiImpl {
  val live: ZLayer[AppConfig with SttpBackend[Task, ZioStreams], Nothing, ShopApi] =
    ZLayer {
      for {
        client <- ZIO.service[SttpBackend[Task, ZioStreams]]
        config <- ZIO.service[AppConfig]
      } yield new ShopApiImpl(client, config.shopApi)
    }
}
