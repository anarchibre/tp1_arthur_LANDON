package object exercises {

  package tp1

  import org.apache.log4j.{Level, Logger}
  import org.apache.spark.sql.SparkSession

  object Exo_1 {
    def main(args: Array[String]): Unit = {
      Logger.getLogger("org").setLevel(Level.OFF)

      println("Hello world")

      val sparkSession = SparkSession.builder().master("local").getOrCreate()
      val rdd = sparkSession.sparkContext.textFile("data/input_data/donnees.csv")

      //Q2
      val films_leo = rdd.filter(elem => elem.contains("Di Caprio"))
      print("Il y a " + films_leo.count() + " films ou joue Leonardo Di Caprio")

      //Q3
      val counts = films_leo.map(item => (item.split(";")(2).toDouble) )
      val moyenne = counts.sum
      print("La moyenne des films de Di Caprio est: " + moyenne/films_leo.count())

      //Q4
      val viewsleo = films_leo.map(item => (item.split(";")(1).toDouble))
      val allviews = rdd.map(item => (item.split(";")(1).toDouble))
      val rateviewsleo = viewsleo.sum() / allviews.sum()
      println(rateviewsleo)

      //Q5
      val acteurs = rdd.map(item => (item.split(";")(3)) )
      acteurs.foreach(println)




    }
  }
  package tp1

  import org.apache.log4j.{Level, Logger}
  import org.apache.spark.sql.functions._
  import org.apache.spark.sql.{DataFrame, SparkSession}

  object Exo_2 {
    def main(args: Array[String]): Unit = {
      Logger.getLogger("org").setLevel(Level.OFF)

      println("Hello world")

      val sparkSession = SparkSession.builder().master("local").getOrCreate()
      val df: DataFrame = sparkSession.read.option("header", false).option("delimiter", ";").option("inferSchema", true).csv("data/input_data/donnees.csv")

      val renamed_df: DataFrame = df.withColumnRenamed("_c0", "nom_film")
        .withColumnRenamed("_c1", "nombre_vues")
        .withColumnRenamed("_c2", "note_film")
        .withColumnRenamed("_c3", "acteur_principal")

      val films_leo: DataFrame = renamed_df.filter(renamed_df("acteur_principal") === "Di Caprio")
      print("Il y a " + films_leo.count() + " films ou joue Leonardo Di Caprio")

      val moyenne_leo: DataFrame = films_leo.groupBy( col1 = "acteur_principal").mean( colNames = "note_film")
      moyenne_leo.show

      val allviews2 = renamed_df.agg(sum("nombre_vues")).first.get(0)

      val pourcentage_views = renamed_df.withColumn(colName = "pourcentage_de_vues", col(colName = "nombre_vues") / allviews2 * 100)
      pourcentage_views.show

      val somme_views  = renamed_df.agg(sum("nombre_vues")).first.get(0).toString.toDouble
      val somme_views_leo = films_leo.agg(sum("nombre_vues")).first.get(0).toString.toDouble

      val pourcentage_vues_ldc: Double = somme_views_leo / somme_views * 100
      print(pourcentage_vues_ldc)

      val rates_acteur = renamed_df.groupBy( col1 = "acteur_principal").mean( colNames = "nombre_vues")
      rates_acteur.show

      val views_acteur = renamed_df.groupBy( col1 = "acteur_principal").mean( colNames = "note_film")
      views_acteur.show



    }
  }

}
