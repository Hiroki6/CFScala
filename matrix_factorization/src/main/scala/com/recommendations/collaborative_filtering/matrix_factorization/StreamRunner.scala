package com.recommendations.collaborative_filtering.matrix_factorization

import java.io.IOException
import java.nio.file.Paths

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Flow, RunnableGraph, Sink}
import com.recommendations.collaborative_filtering.matrix_factorization.models.StreamMatrixFactorization
import com.recommendations.collaborative_filtering.matrix_factorization.preprocessings.{MFSet, StreamMFDGen}

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
  val lineToMFDFlow: Flow[String, Option[MFSet], NotUsed] = Flow.fromFunction[String, Option[MFSet]]{ line => mfdGen.convertRateToMFD(line, '\t') }

  // MFDVectorを学習する
  val sink: Sink[Option[MFSet], Future[Done]] = Sink.foreach[Option[MFSet]]{ mfs => mfs.foreach(m => sMF.fit(m)) }

  def run(filePath: String): Future[Done] = {
    val source = getSource(filePath: String)
    //val stream = source via lineToMFDFlow to sink
    val f = source via lineToMFDFlow
    f.runWith(sink)
    //stream
  }
}