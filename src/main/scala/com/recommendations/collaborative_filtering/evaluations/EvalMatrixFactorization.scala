package com.recommendations.collaborative_filtering.evaluations

import com.recommendations.collaborative_filtering.models.MatrixFactorization
import com.recommendations.collaborative_filtering.preprocessings.{CFD, MFD, MFDIterator}
import com.recommendations.collaborative_filtering.utils.CalculcationUtil._

/**
  * MFの評価クラス
  */
class EvalMatrixFactorization(mfModel: MatrixFactorization) {

  def calcMSE(testMFD: MFD): Double = {
    calcError(testMFD.iterator, 0.0)(mfModel.predict)(calcAbsError)
  }

  def calcRMSE(testMFD: MFD): Double = {
    calcError(testMFD.iterator, 0.0)(mfModel.predict)(calcSquareError)
  }

}
