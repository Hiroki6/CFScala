package com.recommendations.collaborative_filtering.core.infrastructures

import java.io.File

import sbt.io._

import scala.collection.mutable
import scala.collection.mutable.HashMap

class GeneratorSupport {
  val userIdMap: UserIdMap = UserIdMap()
  val itemIdMap: ItemIdMap = ItemIdMap()

  def apply(userFilePath: String, itemFilePath: String, separator: Char) = {
    updateIdMap(userIdMap, userFilePath, separator)
    updateIdMap(itemIdMap, itemFilePath, separator)
  }

  // idと配列のindex更新
  def updateIdMap(idMap: ElementMap[String, Int], filePath: String, separator: Char): Unit = {
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

trait ElementMap[K, V] {
  val value: HashMap[K, V]
  def update(k: K, v: V) = value.update(k, v)
  def size = value.size
  def apply(key: K) = value(key)
}

case class UserIdMap(value: HashMap[String, Int] = new mutable.HashMap[String, Int]) extends ElementMap[String, Int]

case class ItemIdMap(value: HashMap[String, Int] = new mutable.HashMap[String, Int]) extends ElementMap[String, Int]
