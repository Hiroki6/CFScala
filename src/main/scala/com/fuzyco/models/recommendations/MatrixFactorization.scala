package com.fuzyco.models.recommendations

import breeze.linalg.DenseMatrix
import breeze.stats.distributions.Gaussian
import com.fuzyco.preprocessings.MFD

/**
  * Matrix Factorizationモデル　
  * @param mfd
  */
class MatrixFactorization(mfd: MFD, k: Int) {

  // ユーザー重み行列
  val userW = getInitialMatrix()
  // アイテム重み行列
  val itemW = getInitialMatrix()

  def getInitialMatrix() = {
    val normal = Gaussian(0, 1)
    MFW(DenseMatrix.rand(mfd.value.cols, k, normal))
  }

  /**
    * 学習
    * 各ユーザーの配列からデータを取得する部分を並列で処理する
    * @param step
    * @param gamma
    * @param beta
    * @param threshold
    * @return
    */
  def fit(step: Int = 30, gamma: Double = 0.005, beta: Double = 0.02, threshold: Double = 0.1) = ???

  def predict(userId: Int, itemId: Int): Double = userW.value(::, userId) dot itemW.value(::, itemId)

  private def getRatingError(userId: Int, itemId: Int): Double = mfd.value(userId, itemId) - predict(userId, itemId)

}

case class MFW(value: DenseMatrix[Double])
