package com.recommendations.collaborative_filtering.matrix_factorization.preprocessings

import java.io.File

import sbt.io._
import breeze.linalg._
import com.recommendations.collaborative_filtering.core.infrastructures.{CFD, CFDIterator, GeneratorSupport}

/**
  * Matrix Factorizationのデータクラス
  */
class MFDGen extends GeneratorSupport {
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
  def iterator = MFDIter(value.iterator)
}

// DenseMatrix[Double].iteratorをラップしたケースクラス
case class MFDIter(value: Iterator[((Int, Int), Double)]) extends CFDIterator[Double] {
  def next = {
    val data = value.next
    val userId= data._1._1
    val itemId = data._1._2
    val rate = data._2
    (userId, itemId, rate)
  }
}

