package com.recommendations.collaborative_filtering.factorization_machines.models

import breeze.linalg.{CSCMatrix, DenseVector}
import com.recommendations.collaborative_filtering.core.utils.MatrixUtil
import com.recommendations.collaborative_filtering.factorization_machines.preprocessings.Alias.{Rate, RateList}
import com.recommendations.collaborative_filtering.factorization_machines.preprocessings.FMDGen


/**
  * Factorization Machinesモデル
  * @param K
  */
class FactorizationMachines(fmdGen: FMDGen, K: Int) {

  var w_0 = 0.0
  val W: FMW = FMW(getInitialMatrix(fmdGen.featureMap.size, 1))
  val V: FMV = FMV(getInitialMatrix(fmdGen.featureMap.size, K))
  val regs: DenseVector[Double] = DenseVector.zeros[Double](K+2)

  def getInitialMatrix(rows: Int, cols: Int) = {
    MatrixUtil.getRandomSparseMatrix(rows, cols)
  }

  def fit(rateList: RateList, epochs: Int): Unit = {
    (1 to epochs).par.foreach { epoch =>
      rateList.foreach { rate =>
        val error = calcError(rate)
        updateW0(error)
        updateW(error)
        updateV(error)
      }
    }
  }

  def predict(fmd: CSCMatrix[Double]): Double = ???

  def calcError(rate: Rate): Double = {
    val (fmd, label) = fmdGen.getFMDByRate(rate)
    label - predict(fmd)
  }

  def updateW0(error: Double): Unit = ???

  def updateW(error: Double): Unit = ???

  def updateV(error: Double): Unit = ???
}

case class FMW(value: CSCMatrix[Double])

case class FMV(value: CSCMatrix[Double])
