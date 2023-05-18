package com.rosteelton.processor.api

import com.rosteelton.processor.model.Product
import com.rosteelton.processor.utils.AppError
import zio.IO
import zio.stream.ZStream

trait ServiceProviderApi {
  def uploadProducts(products: ZStream[Any, AppError, Product], articlesCount: Int): IO[AppError, Unit]
}