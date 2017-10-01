package com.recommendations.collaborative_filtering.factorization_machines.models

import breeze.linalg._
import com.recommendations.collaborative_filtering.core.utils.MatrixUtil
import com.recommendations.collaborative_filtering.factorization_machines.preprocessings.Alias.{Rate, RateList}
import com.recommendations.collaborative_filtering.factorization_machines.preprocessings.{FMD, FMDGen, FMDIter, FMDVector}

/**
  * Factorization Machinesモデル
  *
  * @param K
  */
class FactorizationMachines(fmdGen: FMDGen, K: Int) {

  type DotSum = Double
  type DotSumSquare = Double
  val ROWS = fmdGen.featureMap.size
  private var w0 = 0.0
  val W: FMW = FMW {
    MatrixUtil.getRandomDenseVector(ROWS)
  }
  val V: FMV = FMV {
    MatrixUtil.getRandomDenseMatrix(ROWS, K)
  }
  val regs: DenseVector[Double] = DenseVector.zeros[Double](K+2)

  private var adagradW0 = 0.0
  private val adagradW = DenseVector.zeros[Double](ROWS)
  private val adagradV = DenseMatrix.zeros[Double](ROWS, K)

  /**
    * 学習
   */
  def fitIterator(rateList: RateList, epochs: Int, eta: Double): Unit = {
    (1 to epochs).foreach { epoch =>
      println(epoch)
      rateList.par.foreach { rate =>
        val fmdVector = fmdGen.getFMDByRate(rate)
        val error = calcError(fmdVector)
        updateW0(error, eta)
        fit(fmdVector.data, error, eta)
      }
    }
  }

  /**
    * 学習
   */
  private def fit(fmd: FMD, error: Double, eta: Double): Unit = {
    @annotation.tailrec
    def go(fmdIter: FMDIter): Unit = {
      if(fmdIter.hasNext) {
        val (index, featureIndex, value) = fmdIter.next
        updateW(featureIndex, error, eta)
        for(f <- 0 until K) {
          updateV(fmd, featureIndex, f, error, eta)
        }
        go(fmdIter)
      }
    }
    go(fmd.iterator)
  }

  def predict(fmd: FMD): Double = this.w0 + calcLinearIterations(fmd) + calcInteractions(fmd)

  private def calcLinearIterations(fmd: FMD): Double = {
    @annotation.tailrec
    def go(fmdIter: FMDIter, sumValue: Double): Double = {
      if(fmdIter.hasNext) {
        val (index, featureIndex, value) = fmdIter.next
        go(fmdIter, sumValue + this.W.value(featureIndex)*value)
      } else sumValue
    }
    go(fmd.iterator, 0.0)
  }

  /**
    * 相互作用の項の計算
    *
    * iterations = 0.0
    * @loop
    * for f in xrange(self.K):
    *   dot_sum = 0.0
    *   dot_sum_square = 0.0
    *   @go
    *   for ix in ixs:
    *     dot_sum += self.V[ix][f] * matrix[ix]
    *     dot_sum_square += self.V[ix][f] * self.V[ix][f] * matrix[ix] * matrix[ix]
    *   iterations += dot_sum * dot_sum - dot_sum_square
    */
  private def calcInteractions(fmd: FMD): Double = {
    @annotation.tailrec
    def go(fmdIter: FMDIter, index: Int, sumTuple: (DotSum, DotSumSquare)): (DotSum, DotSumSquare) = {
      if(fmdIter.hasNext) {
        val (index, featureIndex, value) = fmdIter.next
        val dotSum = this.V.value(featureIndex, index) * value
        val dotSumSquare = math.pow(this.V.value(featureIndex, index), 2) * math.pow(value, 2)
        go(fmdIter, index, (sumTuple._1 + dotSum, sumTuple._2 + dotSumSquare))
      } else sumTuple
    }
    @annotation.tailrec
    def loop(fmd: FMD, index: Int, sumValue: Double): Double = {
      if(index < K) {
        val (dotSum, dotSumSquare) = go(fmd.iterator, index, (0.0, 0.0))
        loop(fmd, index+1, sumValue + (dotSum * dotSum) - dotSumSquare)
      } else {
        sumValue / 2
      }
    }
    loop(fmd, 0, 0.0)
  }

  private def calcError(fmdVector: FMDVector): Double = fmdVector.label - predict(fmdVector.data)

  private def updateW0(error: Double, eta: Double): Unit = {
    val gradValue = 2 * eta * (error + this.regs(0) * this.w0)
    this.adagradW0 += math.pow(gradValue, 2)
    this.w0 -= eta * gradValue / math.sqrt(this.adagradW0)
  }

  private def updateW(featureIndex: Int, error: Double, eta: Double): Unit = {
    val gradValue = 2 * eta * (error + this.regs(1) * this.W.value(featureIndex))
    this.adagradW.update(featureIndex, math.pow(gradValue, 2.0))
    val updateValue = this.W.value(featureIndex) - eta * gradValue / math.sqrt(this.adagradW(featureIndex))
    this.W.value.update(featureIndex, updateValue)
  }

  private def updateV(fmd: FMD, i: Int, f: Int, error: Double, eta: Double): Unit = {
    @annotation.tailrec
    def calcH(fmdIter: FMDIter, sumValue: Double): Double = {
      if(fmdIter.hasNext) {
        val (index, featureIndex, value) = fmdIter.next
        calcH(fmdIter, sumValue + this.V.value(featureIndex, index) * value)
      } else (sumValue - this.V.value(i, f) * fmd.value(0, i)) * fmd.value(0, i)
    }
    val h = calcH(fmd.iterator, 0.0)
    val gradValue = 2 * (error* h + this.regs(2+f)*this.V.value(i, f))
    this.adagradV.update(i, f, math.pow(gradValue, 2))
    val updateValue = this.V.value(i, f) - eta * gradValue / math.sqrt(this.adagradV(i, f))
    this.V.value.update(i, f, updateValue)
  }
}

case class FMW(value: DenseVector[Double])

case class FMV(value: DenseMatrix[Double])
