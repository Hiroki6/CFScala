package com.recommendations.collaborative_filtering.utils

import com.recommendations.collaborative_filtering.preprocessings.CFD
import MatrixUtil._

trait EvalSupport[T] {
  def calcMSE(testMFD: CFD[T]): Double = {
    calcError(testMFD.value.iterator, 0.0)(calcAbsError)
  }

  def calcRMSE(testMFD: CFD[T]): Double = {
    calcError(testMFD.value.iterator, 0.0)(calcSquareError)
  }

  protected def calcError(mfdIterator: Iterator[((Int, Int), T)], error: Double)(f: (Double, Double) => Double): Double
}
