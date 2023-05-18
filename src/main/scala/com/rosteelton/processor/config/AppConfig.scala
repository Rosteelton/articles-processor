package com.rosteelton.processor.config

import com.typesafe.config.ConfigFactory
import sttp.client3.UriContext
import sttp.model.Uri
import zio.config._
import zio.config.magnolia._
import zio.config.typesafe.TypesafeConfig
import zio.{Layer, ZIO}

case class AppConfig(shopApi: HttpClientConfig, serviceProviderApi: HttpClientConfig)
object AppConfig {
  implicit val uriDescriptor: Descriptor[Uri] =
    Descriptor[String].transform(str => uri"$str", _.toString())

  val live: Layer[ReadError[String], AppConfig] =
    TypesafeConfig.fromTypesafeConfig(
      ZIO.attempt(ConfigFactory.load().getConfig("com.rosteelton.processor")),
      descriptor[AppConfig]
    )
}
