package com.recommendations.collaborative_filtering.matrix_factorization.preprocessings

import breeze.linalg.DenseVector

import com.recommendations.collaborative_filtering.core.infrastructures.GeneratorSupport


class StreamMFDGen extends GeneratorSupport {
  def convertRateToMFD(rateLine: String, separator: Char): Option[MFSet] = {
    val splitData = rateLine.split(separator)
    if(splitData.length == 4) {
      val userId = splitData(0)
      val itemId = splitData(1)
      val rate = splitData(2)
      for {
        userIndex <- userIdMap.get(userId)
        itemIndex <- itemIdMap.get(itemId)
      } yield {
        MFSet(userIndex, itemIndex, rate.toDouble)
      }
    } else None
  }
}

case class MFSet(userId: Int, itemId: Int, rate: Double)

