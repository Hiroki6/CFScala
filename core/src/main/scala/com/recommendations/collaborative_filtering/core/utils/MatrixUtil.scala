package com.recommendations.collaborative_filtering.core.utils

import breeze.linalg.{CSCMatrix, DenseMatrix, DenseVector}
import breeze.stats.distributions.Gaussian

object MatrixUtil {
  val normal = Gaussian(0, 0.1)

  // DenseMatrixのノルムの計算
  def calcNorm(vecs: DenseMatrix[Double]) =
    (for (i <- 0 until vecs.cols) yield {val v = vecs(::, i); v dot v}).sum

  def getRandomDenseMatrix(rows: Int, cols: Int): DenseMatrix[Double] = {
    DenseMatrix.rand(rows, cols, normal)
  }

  def getRandomDenseVector(rows: Int): DenseVector[Double] = {
    DenseVector.rand(rows, normal)
  }

  def getRandomSparseMatrix(rows: Int, cols: Int): CSCMatrix[Double] = {
    CSCMatrix.rand(rows, cols, normal)
  }

}