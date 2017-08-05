package com.recommendations.collaborative_filtering.tests

import com.recommendations.collaborative_filtering.evaluations.EvalMatrixFactorization
import com.recommendations.collaborative_filtering.models.MatrixFactorization
import com.recommendations.collaborative_filtering.preprocessings.MFDGenerator
import com.typesafe.scalalogging.Logger

object TestMatrixFactorization extends App {

  val logger = Logger("Test Matrix Factorization")
  val userFilePath = "data/ml-100k/u.user"
  val itemFilePath = "data/ml-100k/u.item"
  val trainFilePath = "data/ml-100k/u1.base"
  val testFilePath = "data/ml-100k/u1.test"
  val mfdGenerator = new MFDGenerator

  logger.info("intialize map")
  mfdGenerator.apply(userFilePath, itemFilePath, '|')

  logger.info("get train MFD")
  val trainMFD = mfdGenerator.getMatrix(trainFilePath, '\t')

  logger.info("initialize")
  val mf = new MatrixFactorization(mfd = trainMFD, K = 100)

  logger.info("fit")
  val start = System.currentTimeMillis
  mf.fitIterator(epochs = 100)
  println((System.currentTimeMillis - start)/1000)

  logger.info("get test MFD")
  val testMFD = mfdGenerator.getMatrix(testFilePath, '\t')

  logger.info("initial eval class")
  val evalMF = new EvalMatrixFactorization(mf)

  logger.info("evaluation")
  println(evalMF.calcRMSE(testMFD))
}
