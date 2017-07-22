package com.recommendations.collaborative_filtering.tests

import com.recommendations.collaborative_filtering.models.MatrixFactorization
import com.recommendations.collaborative_filtering.preprocessings.MFDGenerator
import com.typesafe.scalalogging.Logger

object TestMatrixFactorization extends App {

  val logger = Logger("Test Matrix Factorization")
  val userFilePath = "data/ml-100k/u.user"
  val itemFilePath = "data/ml-100k/u.item"
  val rateFilePath = "data/ml-100k/u1.base"
  val mfdGenerator = new MFDGenerator

  logger.info("intialize map")
  mfdGenerator.apply(userFilePath, itemFilePath, '|')

  logger.info("get MFD")
  val mfd = mfdGenerator.getMatrix(rateFilePath, '\t')

  logger.info("initialize Matrix Factorization")
  val mf = new MatrixFactorization(mfd, 8)

  logger.info("fit Matrix Factorization")
  mf.fitIterator()
}
