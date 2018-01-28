package com.recommendations.collaborative_filtering.matrix_factorization.repositories

import breeze.linalg.DenseMatrix
import com.recommendations.collaborative_filtering.core.infrastructures.{MixInRedisMapper, UsesInRedisMapper}
import com.recommendations.collaborative_filtering.matrix_factorization.models.{MFW, MatrixFactorization}

import com.redis.serialization.Parse.Implicits.parseDouble

/**
  * Redisを用いたMFの永続化
  */
trait MatrixFactorizationRepositoryOnRedis extends MatrixFactorizationRepository with UsesInRedisMapper {
  /**
    * redisからパラメータを取得する
    * redisに保存されていない場合は、ランダムに初期化する
    */
  def getWeightMatrix(key: String, elemCounts: Int, k: Int): MFW = {
    redisMapper.getOneDimMatrix[Double](key).fold(getInitialMatrix(elemCounts, k)) {
      matrix => {
        if(matrix.isEmpty) getInitialMatrix(elemCounts, k)
        else MFW(DenseMatrix.create(elemCounts, k, matrix.toArray))
      }
    }
  }

  def updateParameters(matrixFactorization: MatrixFactorization): Unit = {
    updateWeightMatrix("user_w", matrixFactorization.userW)
    updateWeightMatrix("item_w", matrixFactorization.itemW)
  }

  private[repositories] def updateWeightMatrix(key: String, mfw: MFW): Unit = {
    redisMapper.deleteValues(key)
    redisMapper.rpushValues(key, mfw.value.valuesIterator.toSeq)
  }
}

private object MatrixFactorizationRepositoryOnRedisImpl extends MatrixFactorizationRepositoryOnRedis with MixInRedisMapper