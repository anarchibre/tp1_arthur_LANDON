package exercises

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object Exo_1 {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)

    println("Hello world")

    val sparkSession = SparkSession.builder().master("local").getOrCreate()
    val rdd = sparkSession.sparkContext.textFile("data/donnees_civiles.csv")

    //val header = rdd.first()
    //val rdd_withoutheader = rdd.filter(x => !x.equals(header))

    val rdd_sans_B = rdd.filter(elem => !elem.contains("E"))

    val rdd_nb_enfant = rdd_sans_B.map((elem: String) => elem.split(";")(1))

    val rdd_2enfantsmax = rdd_nb_enfant.filter(elem => elem.>=("3"))

    print("Il reste : " + rdd_2enfantsmax.count() + " personnes")

    //Version 1

    val counts: RDD[(Double, (Double, Double))] = rdd.map(item => (item.split(";")(2).toDouble, (1.0, item.split(";")(1).toDouble)) )
    val countSums: RDD[(Double, (Double, Double))] = counts.reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2 ))
    val keyMeans: RDD[(Double, Double)] = countSums.mapValues(avgCount => avgCount._2 / avgCount._1)
    keyMeans.foreach(println)

    //Version 2

    /*val pairRddKeyBy: RDD[(String, String)] = rdd.keyBy(elem => elem.split(";")(2))
    val withValue = pairRddKeyBy.mapValues(e => (1.0, e))
    val countSums2 = withValue.reduceByKey((x,y) => (x._1 + y._1, x._2 + y._2 ))
    val keyMeans2 = countSums2.mapValues(avgCount => avgCount._2 / avgCount._1)
    keyMeans2.foreach(println)*/

  }
}
