package com.recommendations.collaborative_filtering.matrix_factorization.tests

import akka.actor.ActorSystem
import com.recommendations.collaborative_filtering.matrix_factorization.StreamRunner
import com.recommendations.collaborative_filtering.matrix_factorization.models.StreamMatrixFactorization
import com.recommendations.collaborative_filtering.matrix_factorization.preprocessings.StreamMFDGen
import com.typesafe.scalalogging.Logger
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.util.ByteString

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}


object TestStreamMatrixFactorization extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val logger = Logger("Test Matrix Factorization")
  val userFilePath = "data/ml-100k/u.user"
  val itemFilePath = "data/ml-100k/u.item"
  val trainFilePath = "data/ml-100k/u1.base"
  val testFilePath = "data/ml-100k/u1.test"
  val smfd = new StreamMFDGen

  logger.info("initialize map")
  smfd.apply(userFilePath, itemFilePath, '|')

  logger.info("initialize")
  val mf = new StreamMatrixFactorization(smfd.userIdMap, smfd.itemIdMap, K = 100)

  logger.info("initialize runner")
  val streamRunner = new StreamRunner(smfd, mf)

  logger.info("fit")
  val result = streamRunner.run(testFilePath)
  Await.ready(result, Duration.Inf)
  result.onComplete {
    case Success(_) => println("success")
    case Failure(t) => println(t)
  }
}