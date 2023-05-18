package com.rosteelton.processor.service

import com.rosteelton.processor.utils.AppError
import zio.{IO, ZIO}

trait ArticleProcessor {
  def process(articlesCount: Int): IO[AppError, Unit]
}

object ArticleProcessor {
  def process(articlesCount: Int): ZIO[ArticleProcessor, AppError, Unit] =
    ZIO.environmentWithZIO(_.get.process(articlesCount))
}
