package com.recommendations.collaborative_filtering.matrix_factorization.tests

import com.recommendations.collaborative_filtering.matrix_factorization.evaluations.EvalMatrixFactorization
import com.recommendations.collaborative_filtering.matrix_factorization.models.MatrixFactorization
import com.recommendations.collaborative_filtering.matrix_factorization.preprocessings.MFDGen
import com.typesafe.scalalogging.Logger

object TestMatrixFactorization extends App {

  val logger = Logger("Test Matrix Factorization")
  val userFilePath = "data/ml-100k/u.user"
  val itemFilePath = "data/ml-100k/u.item"
  val trainFilePath = "data/ml-100k/u1.base"
  val testFilePath = "data/ml-100k/u1.test"
  val mfdGen = new MFDGen

  logger.info("initialize map")
  mfdGen.apply(userFilePath, itemFilePath, '|')

  logger.info("get train MFD")
  val trainMFD = mfdGen.getMatrix(trainFilePath, '\t')

  logger.info("initialize")
  val mf = new MatrixFactorization(mfdGen.userIdMap, mfdGen.itemIdMap, K = 100)

  logger.info("fit")
  val start = System.currentTimeMillis
  mf.fitIterator(trainMFD, epochs = 100)
  println((System.currentTimeMillis - start)/1000)

  logger.info("get test MFD")
  val testMFD = mfdGen.getMatrix(testFilePath, '\t')

  logger.info("initial eval class")
  val evalMF = new EvalMatrixFactorization(mf)

  logger.info("evaluation")
  println(evalMF.calcRMSE(testMFD))
}
