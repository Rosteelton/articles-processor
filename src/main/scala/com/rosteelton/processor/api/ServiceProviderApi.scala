package com.rosteelton.processor.api

import com.rosteelton.processor.api.ServiceProviderApi.ServiceProviderApiError
import com.rosteelton.processor.model.Product
import zio.IO

trait ServiceProviderApi {
  def uploadProducts(products: List[Product], articlesCount: Int): IO[ServiceProviderApiError, Unit]
}

object ServiceProviderApi {
  case class ServiceProviderApiError(message: String)
}
