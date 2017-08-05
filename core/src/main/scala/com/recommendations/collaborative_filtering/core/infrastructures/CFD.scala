package com.recommendations.collaborative_filtering.core.infrastructures

import breeze.linalg.DenseMatrix

trait CFD[T] {
  val value: DenseMatrix[T]
}

trait CFDIterator[T] {
  val value: Iterator[((Int, Int), Double)]
  def hasNext = value.hasNext
  def next: (Int, Int, T)
}
