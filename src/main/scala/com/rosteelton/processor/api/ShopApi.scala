package com.rosteelton.processor.api

import com.rosteelton.processor.model.Article
import com.rosteelton.processor.utils.AppError
import zio.stream.ZStream

trait ShopApi {
  def getArticles(limit: Int): ZStream[Any, AppError, Article]
}