package com.recommendations.collaborative_filtering.matrix_factorization.models

import breeze.linalg.DenseMatrix
import com.recommendations.collaborative_filtering.core.infrastructures.{ItemIdMap, UserIdMap}
import com.recommendations.collaborative_filtering.core.utils.MatrixUtil._
import com.recommendations.collaborative_filtering.matrix_factorization.preprocessings.{MFD, MFDIter}
import com.recommendations.collaborative_filtering.matrix_factorization.repositories.MixInMatrixFactorizationRepository
import com.recommendations.collaborative_filtering.matrix_factorization.utils.CalculcationUtil._

/**
  * Matrix Factorizationモデル　
  */
class MatrixFactorization(useIdMap: UserIdMap, itemIdMap: ItemIdMap, K: Int) extends MixInMatrixFactorizationRepository {

  // ユーザー重み行列
  val userW: MFW = matrixFactorizationRepository.getWeightMatrix("user_w", useIdMap.size, K)
  // アイテム重み行列
  val itemW: MFW = matrixFactorizationRepository.getWeightMatrix("item_w", itemIdMap.size, K).t

  /**
    * 学習
    */
  def fitIterator(mfd: MFD, epochs: Int = 30, eta: Double = 0.005, lambda: Double = 0.02, threshold: Double = 0.1): Unit = {
    (1 to epochs).par.foreach { i =>
      fit(mfd.iterator, eta, lambda)
      val allError = getAllError(mfd, lambda)
      println(allError)
    }
    // 学習したパラメータをredisに保存
    matrixFactorizationRepository.updateParameters(this)
  }

  @annotation.tailrec
  private def fit(mfdIter: MFDIter, eta: Double, lambda: Double): Unit = {
    if(mfdIter.hasNext) {
      val (userId, itemId, rate) = mfdIter.next
      if(rate != 0) {
        val error = rate - predict(userId, itemId)
        for(k <- 0 until K) {
          val prevU = userW.value(userId, k)
          userW.value(userId, k) += eta * (2 * error * itemW.value(k, itemId) - lambda * userW.value(userId, k))
          itemW.value(k, itemId) += eta * (2 * error * prevU - lambda * itemW.value(k, itemId))
        }
      }
    fit(mfdIter, eta, lambda)
    }
  }

  def predict(userId: Int, itemId: Int): Double = userW.value(userId, ::) * itemW.value(::, itemId)

  /**
    * 損失関数
    */
  private def getAllError(mfd: MFD, lambda: Double): Double = {
    calcError(mfd.iterator, 0.0)(predict)(calcSquareError) + lambda / 2 * (calcNorm(userW.value) + calcNorm(itemW.value))
  }

  private def getRatingError(correctValue: Double, userId: Int, itemId: Int): Double = correctValue - predict(userId, itemId)
}

// 重み行列のケースクラス
case class MFW(value: DenseMatrix[Double]) {
  def t = this.copy(value.t)
}
