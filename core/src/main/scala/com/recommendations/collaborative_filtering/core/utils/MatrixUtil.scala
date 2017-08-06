package com.recommendations.collaborative_filtering.core.utils

import breeze.linalg.{CSCMatrix, DenseMatrix}
import breeze.stats.distributions.Gaussian

object MatrixUtil {

  // DenseMatrixのノルムの計算
  def calcNorm(vecs: DenseMatrix[Double]) =
    (for (i <- 0 until vecs.cols) yield {val v = vecs(::, i); v dot v}).sum

  def getRandomDenseMatrix(rows: Int, cols: Int): DenseMatrix[Double] = {
    val normal = Gaussian(0, 0.1)
    DenseMatrix.rand(rows, cols, normal)
  }

  def getRandomSparseMatrix(rows: Int, cols: Int): CSCMatrix[Double] = {
    val normal = Gaussian(0, 0.1)
    CSCMatrix.rand(rows, cols, normal)
  }
}