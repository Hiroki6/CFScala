package com.recommendations.collaborative_filtering.utils

import breeze.linalg.DenseMatrix

object MatrixUtil {

  // DenseMatrixのノルムの計算
  def calcNorm(vecs: DenseMatrix[Double]) = {
    (for (i <- 0 until vecs.cols) yield {val v = vecs(::, i); v dot v}).sum
  }

  def calcSquareError(v1: Double, v2: Double) = Math.pow(v1 - v2, 2.0)

  def calcAbsError(v1: Double, v2: Double) = Math.abs(v1 - v2)
}