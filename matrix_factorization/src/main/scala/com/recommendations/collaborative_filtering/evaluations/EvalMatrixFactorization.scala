package com.recommendations.collaborative_filtering.evaluations

import com.recommendations.collaborative_filtering.models.MatrixFactorization
import com.recommendations.collaborative_filtering.preprocessings.{MFD, MFDIterator}
import com.recommendations.collaborative_filtering.utils.CalculcationUtil._

/**
  * MFの評価クラス
  */
class EvalMatrixFactorization(mfModel: MatrixFactorization) {

  def calcMSE(testMFD: MFD): Double = {
    eval(testMFD.iterator, 0.0, 0)(mfModel.predict)(calcAbsError)
  }

  def calcRMSE(testMFD: MFD): Double = {
    eval(testMFD.iterator, 0.0, 0)(mfModel.predict)(calcSquareError)
  }

  @annotation.tailrec
  private def eval(mfdIterator: MFDIterator, error: Double, count: Int)(predictF: (Int, Int) => Double)(errorF: (Double, Double) => Double): Double = {
    if(mfdIterator.hasNext) {
      val (userId, itemId, rate) = mfdIterator.next
      if(rate != 0.0) eval(mfdIterator, error + errorF(rate, predictF(userId, itemId)), count+1)(predictF)(errorF)
      else eval(mfdIterator, error, count)(predictF)(errorF)
    } else Math.sqrt(error/count)
  }
}
