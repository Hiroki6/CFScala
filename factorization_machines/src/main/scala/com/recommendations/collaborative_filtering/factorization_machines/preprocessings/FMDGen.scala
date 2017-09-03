package com.recommendations.collaborative_filtering.factorization_machines.preprocessings

import java.io.File

import breeze.linalg.{CSCMatrix, DenseMatrix, DenseVector}
import com.recommendations.collaborative_filtering.core.infrastructures._
import com.recommendations.collaborative_filtering.factorization_machines.preprocessings.Alias.{Rate, RateList}
import sbt.io._

import scala.collection.mutable.ListBuffer

/**
  * Factorization Machinesのデータクラス
  */
class FMDGen extends GeneratorSupport {
  // 特徴量とindexのマッピング
  val featureMap: FeatureMap = FeatureMap()

  override def apply(userFilePath: String, itemFilePath: String, separator: Char) = {
    super.apply(userFilePath, itemFilePath, separator)
    updateFeatureMap()
  }

  def updateFeatureMap() = {
    updateFeatureByMapData(userIdMap, 0, "u_")
    updateFeatureByMapData(itemIdMap, featureMap.size - 1, "i_")
  }

  def updateFeatureByMapData(mapData: ElementMap[String, Int], headIndex: Int = 0, headString: String = ""): Unit = {
    for((n, i) <- mapData.value) {
      featureMap.update(headString + n, i + headIndex)
    }
  }

  /**
    * 教師データから必要なデータを取り出す
    * @return Seq[(userId: String, itemId: String, rate: Double)]
    */
  def getRateList(rateFilePath: String, separator: Char): RateList = {
    val rateList = ListBuffer[(String, String, Double)]()
    IO.reader(new File(rateFilePath), IO.utf8) { reader =>
      IO.foreachLine(reader) { line =>
        val splitData = line.split(separator)
        val userId = splitData(0)
        val itemId = splitData(1)
        val rate = splitData(2)
        rateList += ((userId, itemId, rate.toDouble))
      }
    }
    rateList.toList
  }

  /**
    * FMD取得
    * @return (FMD, labels: List[Double])
    */
  def getMatrix(rateFilePath: String, separator: Char): (FMD, List[Double]) = {
    val rateList = getRateList(rateFilePath, separator)
    val data = CSCMatrix.zeros[Double](rateList.size, featureMap.size)
    val labels = ListBuffer[Double]()
    rateList.zipWithIndex.foreach { case ((userId: String, itemId: String, rate: Double), index: Int) =>
      data.update(index, getUserIndex(userId), 1.0)
      data.update(index, getItemIndex(itemId), 1.0)
      labels += (rate)
    }
    (FMD(data), labels.toList)
  }

  def getFMD(userId: String, itemId: String): CSCMatrix[Double] = {
    val fmd = new CSCMatrix.Builder[Double](rows = 1, cols = this.featureMap.size)
    //val fmd = DenseVector.zeros[Double](this.featureMap.size)
    fmd.add(0, getUserIndex(userId), 1.0)
    fmd.add(0, getItemIndex(itemId), 1.0)
    fmd.result()
  }

  def getFMDByRate(rate: Rate): FMDVector = {
    FMDVector(FMD(getFMD(rate._1, rate._2)), rate._3)
  }

  def getUserIndex(userId: String) = this.featureMap("u_" + userId)

  def getItemIndex(itemId: String) = this.featureMap("i_" + itemId)
}

case class FMD(value: CSCMatrix[Double]) extends CFD[Double] {
  def iterator = FMDIter(value.activeIterator)
}

case class FMDIter(value: Iterator[((Int, Int), Double)]) extends CFDIterator[Double] {
  def next = {
    val data = value.next
    val index = data._1._1
    val featureIndex = data._1._2
    val featureValue = data._2
    (index, featureIndex, featureValue)
  }
}


/**
  * FM学習用のベクトル
  * @param data
  * @param label
  */
case class FMDVector(data: FMD, label: Double)

