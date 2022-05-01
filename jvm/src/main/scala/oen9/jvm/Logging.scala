package oen9.jvm

import cats.effect.kernel.Sync
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger

trait Logging[F[_]: Sync] {
  given SelfAwareStructuredLogger[F] = Slf4jLogger.getLoggerFromClass(getClass)
  def log = Logger[F]
}
