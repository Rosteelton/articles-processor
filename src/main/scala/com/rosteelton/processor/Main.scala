package com.rosteelton.processor

import com.rosteelton.processor.api.{ServiceProviderImpl, ShopApiImpl, sttpLive}
import com.rosteelton.processor.config.AppConfig
import com.rosteelton.processor.service.{ArticleProcessor, ArticleProcessorImpl}
import com.rosteelton.processor.utils.AppError
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.client3.logging.slf4j.Slf4jLoggingBackend
import zio.logging.LogFormat
import zio.logging.backend.SLF4J
import zio.{ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object Main extends ZIOAppDefault {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    zio.Runtime.removeDefaultLoggers >>> SLF4J.slf4j(LogFormat.colored)

  val program: ZIO[ArticleProcessor, AppError, Unit] = for {
    _ <- ZIO.logInfo("Program started")
    _ <- ArticleProcessor.process(10)
    _ <- ZIO.logInfo("Program succeeded")
  } yield ()

  def run =
    program.provide(
      AppConfig.live,
      sttpLive,
      ServiceProviderImpl.live,
      ShopApiImpl.live,
      ArticleProcessorImpl.live
    )
}
