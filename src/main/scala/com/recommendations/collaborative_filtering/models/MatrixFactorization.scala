package com.recommendations.collaborative_filtering.models

import breeze.linalg.DenseMatrix
import com.recommendations.collaborative_filtering.preprocessings.{MFD, MFDIterator}
import breeze.stats.distributions.Gaussian
import com.recommendations.collaborative_filtering.utils.MatrixUtil._
import com.recommendations.collaborative_filtering.utils.CalculcationUtil._

/**
  * Matrix Factorizationモデル　
  *
  * @param mfd
  */
class MatrixFactorization(mfd: MFD, K: Int) {

  // ユーザー重み行列
  val userW = getInitialMatrix(mfd.value.rows)
  // アイテム重み行列
  val itemW = getInitialMatrix(mfd.value.cols).t

  def getInitialMatrix(elemCounts: Int) = {
    val normal = Gaussian(0, 0.1)
    MFW(DenseMatrix.rand(elemCounts, K, normal))
  }

  /**
    * 学習
    */
  def fitIterator(epochs: Int = 0, eta: Double = 0.005, lambda: Double = 0.02, threshold: Double = 0.1): Unit = {
    (1 to epochs).par.foreach { i =>
      fit(mfd.iterator, eta, lambda)
      val allError = getAllError(lambda)
      println(allError)
    }
  }

  @annotation.tailrec
  private def fit(mfdIterator: MFDIterator, eta: Double, lambda: Double): Unit = {
    if(mfdIterator.hasNext) {
      val (userId, itemId, rate) = mfdIterator.next
      if(rate != 0) {
        val error = rate - predict(userId, itemId)
        for(k <- 0 until K) {
          userW.value(userId, k) += eta * (2 * error * itemW.value(k, itemId) - lambda * userW.value(userId, k))
          itemW.value(k, itemId) += eta * (2 * error * userW.value(userId, k) - lambda * itemW.value(k, itemId))
        }
      }
    fit(mfdIterator, eta, lambda)
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
