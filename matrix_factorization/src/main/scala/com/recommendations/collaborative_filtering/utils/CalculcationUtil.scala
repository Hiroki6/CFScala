package com.recommendations.collaborative_filtering.utils

import com.recommendations.collaborative_filtering.preprocessings.MFDIterator

object CalculcationUtil {

  @annotation.tailrec
  def calcError(mfdIterator: MFDIterator, error: Double)(predictF: (Int, Int) => Double)(errorF: (Double, Double) => Double): Double = {
    if(mfdIterator.hasNext) {
      val (userId, itemId, rate) = mfdIterator.next
      if(rate != 0.0) calcError(mfdIterator, error + errorF(rate, predictF(userId, itemId)))(predictF)(errorF)
      else calcError(mfdIterator, error)(predictF)(errorF)
    } else error
  }

  def calcSquareError(v1: Double, v2: Double) = Math.pow(v1 - v2, 2.0)

  def calcAbsError(v1: Double, v2: Double) = Math.abs(v1 - v2)
}
