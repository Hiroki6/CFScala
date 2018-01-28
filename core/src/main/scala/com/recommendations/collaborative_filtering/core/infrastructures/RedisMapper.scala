package com.recommendations.collaborative_filtering.core.infrastructures

import com.redis._
import com.redis.serialization._
import com.typesafe.config.ConfigFactory

/**
  * Redisを操作するMapperクラス
  */
trait RedisMapper {
  private val config = ConfigFactory.load()

  private val host = config.getString("redis.host")
  private val port = config.getInt("redis.port")

  private val clients = new RedisClientPool(host, port)

  def deleteValues(key: String): Unit = clients.withClient {
    client => {
      client.del(key)
    }
  }

  def setValue[V](key: String, value: V): Unit = clients.withClient {
    client => client.set(key, value)
  }

  def rpushValues[V](key: String, values: Seq[V]): Unit = clients.withClient {
    client => {
      values.foreach { value =>
        client.rpush(key, value)
      }
    }
  }

  def getValue[V](key: String)(implicit format: Format, parse: Parse[V]): Option[V] = clients.withClient {
    client => {
      client.get[V](key)
    }
  }

  def getOneDimMatrix[V](key: String)(implicit format: Format, parse: Parse[V]): Option[Seq[V]] = clients.withClient {
    client => {
      client.lrange[V](key, 0, -1).map { values =>
        values.flatten
      }
    }
  }
}

private object RedisMapperImpl extends RedisMapper

trait UsesInRedisMapper {
  val redisMapper: RedisMapper
}

trait MixInRedisMapper {
  val redisMapper: RedisMapper = RedisMapperImpl
}
