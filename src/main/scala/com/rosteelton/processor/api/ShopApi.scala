package com.rosteelton.processor.api

import com.rosteelton.processor.api.ShopApi.ShopApiError
import com.rosteelton.processor.model.Article
import zio.IO

trait ShopApi {
  def getArticles(limit: Int): IO[ShopApiError, List[Article]]
}

object ShopApi {
  sealed trait ShopApiError {
    def error: String
  }
  object ShopApiError {
    case class ParsingArticleError(error: String) extends ShopApiError
    case class SttpError(error: String)           extends ShopApiError
  }
}
