package com.recommendations.collaborative_filtering.preprocessings

import java.io.File

import sbt.io._
import breeze.linalg._
import collection.mutable.HashMap

/**
  * Matrix Factorizationのデータクラス
  */
class MFDGenerator {
  val userIdMap: HashMap[String, Int] = new HashMap[String, Int]
  val itemIdMap: HashMap[String, Int] = new HashMap[String, Int]

  def apply(userFilePath: String, itemFilePath: String, separator: Char) = {
    updateIdMap(userIdMap, userFilePath, separator)
    updateIdMap(itemIdMap, itemFilePath, separator)
  }

  // idと配列のindex更新
  def updateIdMap(idMap: HashMap[String, Int], filePath: String, separator: Char): Unit = {
    var index = idMap.size
    IO.reader(new File(filePath), IO.utf8) { reader =>
      IO.foreachLine(reader) { line =>
        val id = line.split(separator)(0)
        idMap.update(id, index)
        index += 1
      }
    }
  }

  // MFD取得
  def getMatrix(rateFilePath: String, separator: Char): MFD = {
    val data = DenseMatrix.zeros[Double](userIdMap.size, itemIdMap.size)
    IO.reader(new File(rateFilePath), IO.utf8) { reader =>
      IO.foreachLine(reader) { line =>
        val splitData = line.split(separator)
        val userId = splitData(0)
        val itemId = splitData(1)
        val rate = splitData(2)
        data.update(userIdMap(userId), itemIdMap(itemId), rate.toDouble)
      }
    }
    MFD(data)
  }
}

// 特徴ベクトルのケースクラス
case class MFD(value: DenseMatrix[Double]) extends CFD[Double] {
  def iterator = MFDIterator(value.iterator)
}

// DenseMatrix[Double].iteratorをラップしたケースクラス
case class MFDIterator(value: Iterator[((Int, Int), Double)]) {
  def hasNext = value.hasNext
  def getData = {
    val data = value.next
    val userId= data._1._1
    val itemId = data._1._2
    val rate = data._2
    (userId, itemId, rate)
  }
}