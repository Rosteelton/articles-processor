package com.rosteelton.processor.service

import com.rosteelton.processor.api.{ServiceProviderApi, ShopApi}
import com.rosteelton.processor.model.{Article, Product}
import com.rosteelton.processor.utils.AppError
import zio.stream.ZStream
import zio.{IO, NonEmptyChunk, ZIO, ZLayer}

class ArticleProcessorImpl(shopApi: ShopApi, serviceProviderApi: ServiceProviderApi) extends ArticleProcessor {
  def process(articlesCount: Int): IO[AppError, Unit] = {
    val products = ArticleProcessorImpl
      .convertArticles(
        shopApi
          .getArticles(articlesCount)
      )

    serviceProviderApi
      .uploadProducts(products, articlesCount)
  }
}

object ArticleProcessorImpl {
  val live =
    ZLayer {
      for {
        shopApi     <- ZIO.service[ShopApi]
        providerApi <- ZIO.service[ServiceProviderApi]
      } yield new ArticleProcessorImpl(shopApi, providerApi)
    }

  private[service] def convertArticles(input: ZStream[Any, AppError, Article]): ZStream[Any, AppError, Product] =
    input
      .tapError(err => ZIO.logError(err.getMessage))
      .groupAdjacentBy(_.productId.value)
      .map(_._2)
      .map(foldProductArticles)
      .collectSome

  private[service] def foldProductArticles(productArticles: NonEmptyChunk[Article]): Option[Product] = {
    productArticles
      .foldLeft(Option.empty[Product]) {
        case (result, next) =>
          if (next.stock.value > 0) {
            val newProduct = Product.fromArticle(next)
            result match {
              case Some(currentResult) =>
                if (currentResult.amount.value > newProduct.amount.value)
                  Some(newProduct.copy(sumOfStocks = newProduct.sumOfStocks + currentResult.sumOfStocks))
                else {
                  Some(currentResult.copy(sumOfStocks = currentResult.sumOfStocks + newProduct.sumOfStocks))
                }
              case None => Some(newProduct)
            }
          } else result
      }
  }
}
