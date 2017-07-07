package com.rafaelgarrote.utad.twitterstreaming.spark

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.dayofmonth
import org.apache.spark.sql.functions.month
import org.apache.spark.sql.functions.year
import org.apache.spark.sql.types.TimestampType


class WriterFunctions(self: DataFrame) {

  val TIME = "time"

  def writeDF(format: String, cnf: Map[String, String], partitionEnabled: Boolean = false): Unit = {
    if(!self.rdd.isEmpty) {
      val writer = self.write.format(format).options(cnf).mode(SaveMode.Append)
      if (partitionEnabled) {
        self
          .withColumn("year", year(self.col(TIME).cast(TimestampType)))
          .withColumn("month", month(self.col(TIME).cast(TimestampType)))
          .withColumn("day", dayofmonth(self.col(TIME).cast(TimestampType)))
          .write.format(format).options(cnf).mode(SaveMode.Append).partitionBy("year", "month", "day").save()
      }
      else writer.save()
    }
  }

}

trait GenericWriter {
  implicit def writerFunctions(dataFrame: DataFrame)(implicit session: SparkSession): WriterFunctions =
    new WriterFunctions(dataFrame)
}

object GenericWriter extends GenericWriter
