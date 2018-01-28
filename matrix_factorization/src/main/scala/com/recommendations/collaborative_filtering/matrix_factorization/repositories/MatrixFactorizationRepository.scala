package com.recommendations.collaborative_filtering.matrix_factorization.repositories

import com.recommendations.collaborative_filtering.core.utils.MatrixUtil
import com.recommendations.collaborative_filtering.matrix_factorization.models.{MFW, MatrixFactorization}

/**
  * MFの永続化を担う
  */
trait MatrixFactorizationRepository {
  /**
    * redisからパラメータを取得する
    * redisに保存されていない場合は、ランダムに初期化する
    */
  def getWeightMatrix(key: String, elemCounts: Int, k: Int): MFW

  private[repositories] def getInitialMatrix(elemCounts: Int, k: Int) = {
    MFW(MatrixUtil.getRandomDenseMatrix(elemCounts, k))
  }

  def updateParameters(matrixFactorization: MatrixFactorization): Unit

  private[repositories] def updateWeightMatrix(key: String, mfw: MFW): Unit
}

trait UsesMatrixFactorizationRepository {
  val matrixFactorizationRepository: MatrixFactorizationRepository
}

trait MixInMatrixFactorizationRepository {
  val matrixFactorizationRepository: MatrixFactorizationRepository = MatrixFactorizationRepositoryOnRedisImpl
}
