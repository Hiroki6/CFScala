package com.recommendations.collaborative_filtering.models

import breeze.linalg.DenseMatrix
import com.recommendations.collaborative_filtering.preprocessings.MFD
import breeze.stats.distributions.Gaussian
import com.recommendations.collaborative_filtering.utils.MatrixUtil._

import scala.annotation.tailrec

/**
  * Matrix Factorizationモデル　
  *
  * @param mfd
  */
class MatrixFactorization(mfd: MFD, k: Int) {

  // ユーザー重み行列
  val userW = getInitialMatrix(mfd.value.rows)
  // アイテム重み行列
  val itemW = getInitialMatrix(mfd.value.cols)

  def getInitialMatrix(elemCounts: Int) = {
    val normal = Gaussian(0, 1)
    MFW(DenseMatrix.rand(k, elemCounts, normal))
  }

  /**
    * 学習
    * 各ユーザーの配列からデータを取得する部分を並列で処理する
    * @param epochs
    * @param gamma
    * @param beta
    * @param threshold
    * @return
    */
  def fitIterator(epochs: Int = 1000, gamma: Double = 0.005, beta: Double = 0.02, threshold: Double = 0.1): Unit = {
    (1 to epochs).par.foreach { i =>
      val forIterator = mfd.value.iterator
      go(forIterator)
      val allError = getAllError(beta)
      println(allError)
    }
    def go(forIterator: Iterator[((Int, Int), Double)]): Unit = {
      if(forIterator.hasNext) {
        val data = forIterator.next
        val userId = data._1._1
        val itemId = data._1._2
        val rate = data._2
        if(rate != 0) {
          val error = rate - predict(userId, itemId)
          userW.value(::, userId) += gamma * (error * itemW.value(::, itemId) - beta * userW.value(::, userId))
          itemW.value(::, itemId) += gamma * (error * userW.value(::, userId) - beta * itemW.value(::, itemId))
        }
        go(forIterator)
      }
    }
  }

  def fitLoop(epochs: Int = 30, gamma: Double = 0.005, beta: Double = 0.02, threshold: Double = 0.1): Unit = {
    (1 to epochs).foreach { epoch =>
      for(userId <- 0 to mfd.value.rows-1) {
        for(itemId <- 0 to mfd.value.cols-1) {
          if(mfd.value(userId, itemId) != 0.0) {
            val error = getRatingError(userId, itemId)
            userW.value(::, userId) += gamma * (error * itemW.value(::, itemId) - beta * userW.value(::, userId))
            itemW.value(::, itemId) += gamma * (error * userW.value(::, userId) - beta * itemW.value(::, itemId))
          }
        }
      }
      val allError = getAllError(beta)
      println(allError)
    }
  }

  def predict(userId: Int, itemId: Int): Double = userW.value(::, userId) dot itemW.value(::, itemId)

  /**
    * 損失関数
    */
  private def getAllError(beta: Double): Double = {
    @annotation.tailrec
    def calcError(forIterator: Iterator[((Int, Int), Double)], error: Double): Double = {
      if(forIterator.hasNext) {
        val data = forIterator.next
        val userId = data._1._1
        val itemId = data._1._2
        val rate = data._2
        if(rate != 0.0) {
          calcError(forIterator, error + Math.pow(rate - predict(userId, itemId), 2.0))
        } else {
          calcError(forIterator, error)
        }
      } else {
        error
      }
    }
    calcError(mfd.value.iterator, 0.0) + beta * (calcNorm(userW.value) + calcNorm(itemW.value))
  }

  private def getRatingError(userId: Int, itemId: Int): Double = mfd.value(userId, itemId) - predict(userId, itemId)

}

// 重み行列のケースクラス
case class MFW(value: DenseMatrix[Double])
