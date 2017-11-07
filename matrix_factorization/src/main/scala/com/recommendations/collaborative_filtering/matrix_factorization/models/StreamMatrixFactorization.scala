package com.recommendations.collaborative_filtering.matrix_factorization.models

import com.recommendations.collaborative_filtering.core.infrastructures.{ItemIdMap, UserIdMap}
import com.recommendations.collaborative_filtering.core.utils.MatrixUtil
import com.recommendations.collaborative_filtering.matrix_factorization.preprocessings.MFSet

/**
  * Matrix Factorizationモデル　
  */
class StreamMatrixFactorization(useIdMap: UserIdMap, itemIdMap: ItemIdMap, K: Int) {

  // ユーザー重み行列
  val userW: MFW = getInitialMatrix(useIdMap.size)
  // アイテム重み行列
  val itemW: MFW = getInitialMatrix(itemIdMap.size).t

  def getInitialMatrix(elemCounts: Int) = {
    MFW(MatrixUtil.getRandomDenseMatrix(elemCounts, K))
  }

  def fit(mfs: MFSet, eta: Double = 0.005, lambda: Double = 0.02, threshold: Double = 0.1): Unit = {
    val error = mfs.rate - predict(mfs.userId, mfs.itemId)
    for(k <- 0 until K) {
      val prevU = userW.value(mfs.userId, k)
      userW.value(mfs.userId, k) += eta * (2 * error * itemW.value(k, mfs.itemId) - lambda * userW.value(mfs.userId, k))
      itemW.value(k, mfs.itemId) += eta * (2 * error * prevU - lambda * itemW.value(k, mfs.itemId))
    }
  }

  def predict(userId: Int, itemId: Int): Double = userW.value(userId, ::) * itemW.value(::, itemId)

}
