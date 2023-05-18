package com.rosteelton.processor

import com.rosteelton.processor.api.{ServiceProviderImpl, ShopApiImpl, sttpLive}
import com.rosteelton.processor.config.AppConfig
import com.rosteelton.processor.service.{ArticleProcessor, ArticleProcessorImpl}
import zio._
import zio.logging.LogFormat
import zio.logging.backend.SLF4J

object Main extends ZIOAppDefault {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    zio.Runtime.removeDefaultLoggers >>> SLF4J.slf4j(LogFormat.colored)

  private val program: ZIO[ArticleProcessor, Exception, Unit] = for {
    _ <- ZIO.logInfo("Program started")
    _ <- Console.printLine("Write articles number")
    articlesCount <-
      Console.readLine
        .map(_.toIntOption)
        .someOrFail(new IllegalArgumentException("Wrong number format"))
    _ <- ArticleProcessor.process(articlesCount)
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
