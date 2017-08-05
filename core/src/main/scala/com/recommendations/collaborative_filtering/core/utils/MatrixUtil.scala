package com.recommendations.collaborative_filtering.core.utils

import breeze.linalg.DenseMatrix

object MatrixUtil {

  // DenseMatrixのノルムの計算
  def calcNorm(vecs: DenseMatrix[Double]) =
    (for (i <- 0 until vecs.cols) yield {val v = vecs(::, i); v dot v}).sum

}