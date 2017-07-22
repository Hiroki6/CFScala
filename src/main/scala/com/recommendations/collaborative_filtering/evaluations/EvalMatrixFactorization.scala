package com.recommendations.collaborative_filtering.evaluations

import com.recommendations.collaborative_filtering.models.MatrixFactorization
import com.recommendations.collaborative_filtering.preprocessings.CFD
import com.recommendations.collaborative_filtering.utils.MatrixUtil._

/**
  * MFの評価クラス
  */
class EvalMatrixFactorization(mfModel: MatrixFactorization) {

  def calcMSE(testMFD: CFD[Double]): Double = {
    calcError(testMFD.value.iterator, 0.0)(calcAbsError)
  }

  def calcRMSE(testMFD: CFD[Double]): Double = {
    calcError(testMFD.value.iterator, 0.0)(calcSquareError)
  }

  @annotation.tailrec
  private def calcError(mfdIterator: Iterator[((Int, Int), Double)], error: Double)(f: (Double, Double) => Double): Double = {
    if(mfdIterator.hasNext) {
      val data = mfdIterator.next
      val userId = data._1._1
      val itemId = data._1._2
      val rate = data._2
      if(rate != 0.0) calcError(mfdIterator, error + f(rate, mfModel.predict(userId, itemId)))(f)
      else calcError(mfdIterator, error)(f)
    } else error
  }
}
