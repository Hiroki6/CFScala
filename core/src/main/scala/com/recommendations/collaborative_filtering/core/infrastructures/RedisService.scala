package com.recommendations.collaborative_filtering.core.infrastructures

import breeze.linalg.{DenseMatrix, DenseVector}
import com.redis._

trait RedisService {
  private val HOST = "localhost"
  private val PORT = 6379

  val redisClient = new RedisClient(HOST, PORT)

  def saveScalarValue[T](key: String, field: String, value: T) = {
    redisClient.hset(key, field, value)
  }

  def saveOneDimArray(key: String, param: DenseVector[Double]) = {
    (0 to param.length).foreach{ index =>
      redisClient.rpush(key, param(index))
    }
  }

  def saveTwoDimArray(preKey: String, params: DenseMatrix[Double]) = {
    (0 to params.rows).foreach { row =>
      val key = preKey + row.toString
      (0 to params.cols).foreach { col =>
        redisClient.rpush(key, params(row, col))
      }
    }
  }

}

object RedisServiceImpl extends RedisService

trait UsesRedisService {
  val redisService: RedisService
}

trait MixInRedisService {
  val redisService: RedisService = RedisServiceImpl
}
