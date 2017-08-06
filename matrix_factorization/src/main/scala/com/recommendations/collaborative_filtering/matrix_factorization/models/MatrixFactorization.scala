package com.recommendations.collaborative_filtering.matrix_factorization.models

import breeze.linalg.DenseMatrix
import com.recommendations.collaborative_filtering.core.utils.MatrixUtil
import com.recommendations.collaborative_filtering.core.utils.MatrixUtil._
import com.recommendations.collaborative_filtering.matrix_factorization.preprocessings.{MFD, MFDIter}
import com.recommendations.collaborative_filtering.matrix_factorization.utils.CalculcationUtil._

/**
  * Matrix Factorizationモデル　
  */
class MatrixFactorization(mfd: MFD, K: Int) {

  // ユーザー重み行列
  val userW: MFW = getInitialMatrix(mfd.value.rows)
  // アイテム重み行列
  val itemW: MFW = getInitialMatrix(mfd.value.cols).t

  def getInitialMatrix(elemCounts: Int) = {
    MFW(MatrixUtil.getRandomDenseMatrix(elemCounts, K))
  }

  /**
    * 学習
    */
  def fitIterator(epochs: Int = 30, eta: Double = 0.005, lambda: Double = 0.02, threshold: Double = 0.1): Unit = {
    (1 to epochs).par.foreach { i =>
      fit(mfd.iterator, eta, lambda)
      val allError = getAllError(lambda)
      println(allError)
    }
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

  def fitLoop(epochs: Int = 30, eta: Double = 0.005, lambda: Double = 0.02, threshold: Double = 0.1): Unit = {
    (1 to epochs).par.foreach { epoch =>
      for(userId <- 0 until mfd.value.rows) {
        for(itemId <- 0 until mfd.value.cols) {
          if(mfd.value(userId, itemId) != 0.0) {
            val error = getRatingError(userId, itemId)
            for(k <- 0 until K) {
              userW.value(userId, k) += eta * (2 * error * itemW.value(k, itemId) - lambda * userW.value(userId, k))
              itemW.value(k, itemId) += eta * (2 * error * userW.value(userId, k) - lambda * itemW.value(k, itemId))
            }
          }
        }
      }
      val allError = getAllError(lambda)
      println(allError)
    }
  }

  def predict(userId: Int, itemId: Int): Double = userW.value(userId, ::) * itemW.value(::, itemId)

  /**
    * 損失関数
    */
  private def getAllError(lambda: Double): Double = {
    calcError(mfd.iterator, 0.0)(predict)(calcSquareError) + lambda / 2 * (calcNorm(userW.value) + calcNorm(itemW.value))
  }

  private def getRatingError(userId: Int, itemId: Int): Double = mfd.value(userId, itemId) - predict(userId, itemId)

}

// 重み行列のケースクラス
case class MFW(value: DenseMatrix[Double]) {
  def t = this.copy(value.t)
}
