package com.rosteelton.processor.api

import com.rosteelton.processor.config.{AppConfig, HttpClientConfig}
import com.rosteelton.processor.model.Product
import com.rosteelton.processor.utils.{AppError, CsvHelper}
import sttp.capabilities.zio.ZioStreams
import sttp.client3.{SttpBackend, _}
import sttp.model.MediaType
import zio.stream.ZStream
import zio.{Chunk, IO, Task, ZIO, ZLayer}

class ServiceProviderImpl(sttpBackend: SttpBackend[Task, ZioStreams], config: HttpClientConfig)
    extends ServiceProviderApi {
  def uploadProducts(
      products: ZStream[Any, AppError, Product],
      articlesCount: Int
  ): IO[AppError, Unit] = {

    val productStreamBytes = (ZStream(Product.header) ++ products
      .map(_.toCsvLine))
      .map(CsvHelper.withLineTerminator)
      .map(s => Chunk.fromArray(s.getBytes()))
      .flattenChunks

    val request = basicRequest
      .response(asString)
      .put(uri"${config.baseUrl}/products/$articlesCount")
      .streamBody(ZioStreams)(productStreamBytes)
      .contentType(MediaType.TextCsv)

    for {
      response <- sttpBackend.send(request).mapError(e => AppError.ServiceProviderError(e.getMessage))
      result <- response.body match {
        case Left(value) => ZIO.fail(AppError.ServiceProviderError(value))
        case Right(_)    => ZIO.unit
      }
    } yield result
  }
}

object ServiceProviderImpl {
  val live: ZLayer[AppConfig with SttpBackend[Task, ZioStreams], Nothing, ServiceProviderApi] =
    ZLayer {
      for {
        client <- ZIO.service[SttpBackend[Task, ZioStreams]]
        config <- ZIO.service[AppConfig]
      } yield new ServiceProviderImpl(client, config.serviceProviderApi)
    }
}
