package com.recommendations.collaborative_filtering.matrix_factorization

import java.io.IOException
import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Flow, Sink}
import com.recommendations.collaborative_filtering.matrix_factorization.models.StreamMatrixFactorization
import com.recommendations.collaborative_filtering.matrix_factorization.preprocessings.{MFSet, StreamMFDGen}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StreamRunner(mfdGen: StreamMFDGen, sMF: StreamMatrixFactorization) {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  def getSource(filePath: String) = {
    FileIO.fromPath(Paths.get(filePath))
      .map(_.utf8String)
      .recover {
        case e: IOException => s"invalid file, ${e}"
      }
  }

  // ファイルをMFDVectorに変換するFlow
  val lineToMFDFlow = Flow[String].map{ line => mfdGen.convertRateToMFD(line, '|') }

  // MFDVectorを学習する
  val sink = Sink.foreach[MFSet]{ mfs => sMF.fit(mfs) }

  def run(filePath: String): Future[IOResult] = {
    val source = getSource(filePath: String)
    val stream = source via lineToMFDFlow to sink
    stream.run()
  }
}