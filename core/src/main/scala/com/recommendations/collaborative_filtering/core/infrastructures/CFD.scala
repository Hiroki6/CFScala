package com.recommendations.collaborative_filtering.core.infrastructures

import breeze.linalg.{DenseMatrix, Matrix}

trait CFD[T] {
  val value: Matrix[T]
}

trait CFDIterator[T] {
  val value: Iterator[((Int, Int), Double)]
  def hasNext = value.hasNext
  def next: (Int, Int, T)
}

case class CFW[T](value: DenseMatrix[T]) {
  def t = this.copy(value.t)
}
