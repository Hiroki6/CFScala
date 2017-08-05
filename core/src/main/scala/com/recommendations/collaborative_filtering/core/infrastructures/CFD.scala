package com.recommendations.collaborative_filtering.core.infrastructures

import breeze.linalg.DenseMatrix

trait CFD[T] {
  val value: DenseMatrix[T]
}
