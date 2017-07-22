package com.recommendations.collaborative_filtering.utils

import breeze.linalg.DenseMatrix

object MatrixUtil {
  def calcNorm(vecs: DenseMatrix[Double]) = {
    (for (i <- 0 until vecs.cols) yield {val v = vecs(::, i); v dot v}).sum
  }
}