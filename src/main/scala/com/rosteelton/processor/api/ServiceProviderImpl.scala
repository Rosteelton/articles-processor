package com.rosteelton.processor.api

import com.rosteelton.processor.api.ServiceProviderApi.ServiceProviderApiError
import com.rosteelton.processor.config.{AppConfig, HttpClientConfig}
import com.rosteelton.processor.model.Product
import com.rosteelton.processor.utils.CsvHelper._
import sttp.client3.{SttpBackend, _}
import sttp.model.MediaType
import zio.{IO, Task, ZIO, ZLayer}

class ServiceProviderImpl(sttpBackend: SttpBackend[Task, Any], config: HttpClientConfig) extends ServiceProviderApi {
  def uploadProducts(products: List[Product], articlesCount: Int): IO[ServiceProviderApiError, Unit] = {

    val request = basicRequest
      .response(asString)
      .put(uri"${config.baseUrl}/products/$articlesCount")
      .body((Product.header :: products.map(_.toCsvLine)).mkString(Format.lineTerminator))
      .contentType(MediaType.TextCsv)

    for {
      _ <- ZIO.logInfo((Product.header :: products.map(_.toCsvLine)).mkString(Format.lineTerminator))
      response <- sttpBackend.send(request).mapError(e => ServiceProviderApiError(e.getMessage))
      result <- response.body match {
        case Left(value) => ZIO.fail(ServiceProviderApiError(value))
        case Right(_)    => ZIO.unit
      }
    } yield result
  }
}

object ServiceProviderImpl {
  val live: ZLayer[AppConfig with SttpBackend[Task, Any], Nothing, ServiceProviderApi] =
    ZLayer {
      for {
        client <- ZIO.service[SttpBackend[Task, Any]]
        config <- ZIO.service[AppConfig]
      } yield new ServiceProviderImpl(client, config.serviceProviderApi)
    }
}
