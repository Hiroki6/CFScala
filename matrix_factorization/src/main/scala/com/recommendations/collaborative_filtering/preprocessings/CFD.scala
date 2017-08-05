package com.recommendations.collaborative_filtering.preprocessings

import breeze.linalg.DenseMatrix

trait CFD[T] {
  val value: DenseMatrix[T]
}
