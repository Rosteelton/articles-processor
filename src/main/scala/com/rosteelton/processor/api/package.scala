package com.rosteelton.processor

import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.client3.logging.slf4j.Slf4jLoggingBackend
import zio.ZLayer

package object api {
  val sttpLive =
    ZLayer.scoped {
      HttpClientZioBackend.scoped().map(Slf4jLoggingBackend(_))
    }
}
