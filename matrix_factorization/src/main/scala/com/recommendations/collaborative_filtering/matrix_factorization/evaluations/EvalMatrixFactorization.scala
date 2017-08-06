package com.recommendations.collaborative_filtering.matrix_factorization.evaluations

import com.recommendations.collaborative_filtering.matrix_factorization.models.MatrixFactorization
import com.recommendations.collaborative_filtering.matrix_factorization.preprocessings.{MFD, MFDIter}
import com.recommendations.collaborative_filtering.matrix_factorization.utils.CalculcationUtil._

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
  private def eval(mfdIter: MFDIter, error: Double, count: Int)(predictF: (Int, Int) => Double)(errorF: (Double, Double) => Double): Double = {
    if(mfdIter.hasNext) {
      val (userId, itemId, rate) = mfdIter.next
      if(rate != 0.0) eval(mfdIter, error + errorF(rate, predictF(userId, itemId)), count+1)(predictF)(errorF)
      else eval(mfdIter, error, count)(predictF)(errorF)
    } else Math.sqrt(error/count)
  }
}
