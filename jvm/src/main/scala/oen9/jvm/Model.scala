package oen9.jvm

import io.circe.Codec

object Model:
  case class SrvInfo(hostname: String) derives Codec.AsObject
