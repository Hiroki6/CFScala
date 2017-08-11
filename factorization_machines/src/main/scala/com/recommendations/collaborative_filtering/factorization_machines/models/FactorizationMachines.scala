package com.recommendations.collaborative_filtering.factorization_machines.models

import breeze.linalg.CSCMatrix
import com.recommendations.collaborative_filtering.core.utils.MatrixUtil
import com.recommendations.collaborative_filtering.factorization_machines.preprocessings.FMD

import scala.collection.mutable.HashMap

/**
  * Factorization Machinesモデル
  * @param fmd
  * @param K
  */
class FactorizationMachines(fmd: FMD, labels: List[Double], featureMap: HashMap[String, Int], K: Int) {

  var w_0 = 0.0
  val W: FMW = FMW(getInitialMatrix(fmd.value.cols, 1))
  val V: FMV = FMV(getInitialMatrix(fmd.value.cols, K))

  def getInitialMatrix(rows: Int, cols: Int) = {
    MatrixUtil.getRandomSparseMatrix(rows, cols)
  }
}

case class FMW(value: CSCMatrix[Double])

case class FMV(value: CSCMatrix[Double])
