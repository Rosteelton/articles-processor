package com.rosteelton.processor.utils

import scala.util.control.NoStackTrace

sealed abstract class AppError(error: String) extends Exception(error) with NoStackTrace
object AppError {
  case class ShopApiError(error: String)         extends AppError(error)
  case class ServiceProviderError(error: String) extends AppError(error)
}
