package com.recommendations.collaborative_filtering.matrix_factorization.preprocessings

import breeze.linalg.DenseVector

import com.recommendations.collaborative_filtering.core.infrastructures.GeneratorSupport


class StreamMFDGen extends GeneratorSupport {
  def convertRateToMFD(rateLine: String, separator: Char): MFSet = {
    val splitData = rateLine.split(separator)
    val userId = splitData(0)
    val itemId = splitData(1)
    val rate = splitData(2)
    MFSet(userIdMap(userId), itemIdMap(itemId), rate.toDouble)
  }
}

case class MFSet(userId: Int, itemId: Int, rate: Double)

