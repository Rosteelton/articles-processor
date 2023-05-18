package com.rosteelton.processor.service

import com.rosteelton.processor.api.{ServiceProviderApi, ShopApi}
import com.rosteelton.processor.model.{Article, Product}
import com.rosteelton.processor.utils.AppError
import zio.{IO, ZIO, ZLayer}

class ArticleProcessorImpl(shopApi: ShopApi, serviceProviderApi: ServiceProviderApi) extends ArticleProcessor {
  def process(articlesCount: Int): IO[AppError, Unit] =
    for {
      articles <-
        shopApi
          .getArticles(articlesCount)
          .tapError(err => ZIO.logError(err.error))
          .mapError(err => AppError.ShopApiError(err.error))
      _ <- ZIO.logInfo(s"Some arcticles: ${articles.take(10).mkString("\n")}")
      products = ArticleProcessorImpl.convertToProducts(articles)
      _ <-
        serviceProviderApi
          .uploadProducts(products, articlesCount)
          .mapError(err => AppError.ServiceProviderError(err.message))
          .when(articles.nonEmpty)
    } yield ()
}

object ArticleProcessorImpl {
  val live =
    ZLayer {
      for {
        shopApi     <- ZIO.service[ShopApi]
        providerApi <- ZIO.service[ServiceProviderApi]
      } yield new ArticleProcessorImpl(shopApi, providerApi)
    }

  private[service] def convertToProducts(articles: List[Article]): List[Product] = {
    articles
      .groupMapReduce(_.productId.value)(Product.fromArticle) {
        case (acc, next) =>
          if (next.amount.value < acc.amount.value && next.sumOfStocks > 0) {
            next.copy(sumOfStocks = next.sumOfStocks + acc.sumOfStocks)
          } else acc.copy(sumOfStocks = acc.sumOfStocks + next.sumOfStocks)
      }
      .values
      .filter(_.sumOfStocks > 0)
      .toList
  }
}
