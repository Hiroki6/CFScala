package com.recommendations.collaborative_filtering.factorization_machines.preprocessings

import java.io.File

import breeze.linalg.DenseMatrix
import com.recommendations.collaborative_filtering.core.infrastructures.{CFD, CFDIterator, GeneratorSupport}
import sbt.io._

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

/**
  * Factorization Machinesのデータクラス
  */
class FMDGen extends GeneratorSupport {
  // 特徴量とindexのマッピング
  val featureMap: HashMap[String, Int] = new HashMap[String, Int]

  def updateFeatureMap() = {
    updateFeatureByMapData(userIdMap, 0, "u_")
    updateFeatureByMapData(itemIdMap, featureMap.size - 1, "i_")
  }

  def updateFeatureByMapData(mapData: HashMap[String, Int], headIndex: Int = 0, headString: String = ""): Unit = {
    for((n, i) <- mapData) {
      featureMap.update(headString + n, i + headIndex)
    }
  }

  /**
    * 教師データから必要なデータを取り出す
    * @return Seq[(userId: String, itemId: String, rate: Double)]
    */
  def getRateList(rateFilePath: String, separator: Char): List[(String, String, Double)] = {
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
    updateFeatureMap()
    println(featureMap)
    val rateList = getRateList(rateFilePath, separator)
    val data = DenseMatrix.zeros[Double](rateList.size, featureMap.size)
    val labels = ListBuffer[Double]()
    rateList.zipWithIndex.foreach { case ((userId: String, itemId: String, rate: Double), index: Int) =>
      data.update(index, featureMap("u_" + userId), 1.0)
      data.update(index, featureMap("i_" + itemId), 1.0)
      labels += (rate)
    }
    (FMD(data), labels.toList)
  }
}

case class FMD(value: DenseMatrix[Double]) extends CFD[Double] {
  def iterator = FMDIter(value.iterator)
}

case class FMDIter(value: Iterator[((Int, Int), Double)]) extends CFDIterator[Double] {
  def next = {
    val data = value.next
    val index = data._1._1
    val feature = data._1._2
    val featureValue = data._2
    (index, feature, featureValue)
  }
}