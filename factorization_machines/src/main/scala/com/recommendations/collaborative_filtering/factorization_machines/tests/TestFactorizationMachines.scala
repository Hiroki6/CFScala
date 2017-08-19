package com.recommendations.collaborative_filtering.factorization_machines.tests

import com.recommendations.collaborative_filtering.factorization_machines.models.FactorizationMachines
import com.recommendations.collaborative_filtering.factorization_machines.preprocessings.FMDGen
import com.typesafe.scalalogging.Logger

object TestFactorizationMachines extends App {

  val logger = Logger("Test Factorization Machines")
  val userFilePath = "data/ml-100k/u.user"
  val itemFilePath = "data/ml-100k/u.item"
  val trainFilePath = "data/ml-100k/u1.base"
  val testFilePath = "data/ml-100k/u1.test"
  val fmdGen = new FMDGen

  logger.info("initialize map")
  fmdGen.apply(userFilePath, itemFilePath, '|')

  logger.info("get train FM Model")
  val fm = new FactorizationMachines(fmdGen, 8)

  logger.info("get train FMD")
  val ratelist = fmdGen.getRateList(trainFilePath, '\t')
}
