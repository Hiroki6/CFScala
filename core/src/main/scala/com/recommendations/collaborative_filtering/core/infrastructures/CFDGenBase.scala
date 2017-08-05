package com.recommendations.collaborative_filtering.core.infrastructures

import java.io.File

import sbt.io._

import scala.collection.mutable.HashMap

class GeneratorSupport {
  val userIdMap: HashMap[String, Int] = new HashMap[String, Int]
  val itemIdMap: HashMap[String, Int] = new HashMap[String, Int]

  def apply(userFilePath: String, itemFilePath: String, separator: Char) = {
    updateIdMap(userIdMap, userFilePath, separator)
    updateIdMap(itemIdMap, itemFilePath, separator)
  }

  // idと配列のindex更新
  def updateIdMap(idMap: HashMap[String, Int], filePath: String, separator: Char): Unit = {
    var index = idMap.size
    IO.reader(new File(filePath), IO.utf8) { reader =>
      IO.foreachLine(reader) { line =>
        val id = line.split(separator)(0)
        idMap.update(id, index)
        index += 1
      }
    }
  }

}
