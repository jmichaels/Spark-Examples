// This assumes that you already have the MovieLens data
// imported into a SQL database.
// If not:
// (For an unsecured local mysql install)
//
//    mysql -u root
//    CREATE DATABASE movielens;
//
//    wget https://raw.githubusercontent.com/ankane/movielens.sql/master/movielens.sql
//    mysql -u root movielens < movielens.sql

import org.apache.spark.sql.SparkSession

object ReadFromMySQLWithJoinsExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("Read From MySQL With Joins Example")
      .master("local[*]")
      .getOrCreate()

    val ratingsDf = spark.read
      .format("jdbc")
      .option("url", "jdbc:mysql://127.0.0.1/movielens?serverTimezone=GMT-6")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable",  "ratings")
      .option("user", "root")
      .option("password", "")
      .load()

    ratingsDf.show

    val moviesDf = spark.read
      .format("jdbc")
      .option("url", "jdbc:mysql://127.0.0.1/movielens?serverTimezone=GMT-6")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable",  "movies")
      .option("user", "root")
      .option("password", "")
      .load()
      .withColumnRenamed("id", "movies_id")

    val ratingsWithMovieNamesDf = ratingsDf.join(
      moviesDf,
      ratingsDf("movie_id") === moviesDf("movies_id"),
      "inner"
    )

    ratingsWithMovieNamesDf.show()

  }



}
