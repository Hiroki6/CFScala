package com.recommendations.collaborative_filtering.preprocessings

import java.io.File

import sbt.io._
import breeze.linalg._

/**
  * Matrix Factorizationのデータクラス
  */
class MFData {
  val userIdMap: Map[String, Int] = Map.empty[String, Int]
  val itemIdMap: Map[String, Int] = Map.empty[String, Int]

  def apply(userFilePass: String, itemFilePass: String) = {
    updateIdMap(userIdMap, userFilePass)
    updateIdMap(itemIdMap, itemFilePass)
  }

  def updateIdMap(idMap: Map[String, Int], filePass: String): Unit = {
    var index = idMap.size
    IO.reader(new File(filePass), IO.utf8) { reader =>
      IO.foreachLine(reader) { line =>
        val id = line.split("::")(0)
        idMap.updated(id, index)
        index += 1
      }
    }
  }

  def getMatrix(rateFilePass: String): MFD = {
    val data = DenseMatrix.zeros[Double](userIdMap.size, itemIdMap.size)
    IO.reader(new File(rateFilePass), IO.utf8) { reader =>
      IO.foreachLine(reader) { line =>
        val splitData = line.split("::")
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
case class MFD(value: DenseMatrix[Double])
