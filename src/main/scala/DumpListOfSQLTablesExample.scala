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

import org.apache.spark.sql.{SaveMode, SparkSession}

object DumpListOfSQLTablesExample {
  def readSQLTableDumpToParquet(table: String, spark: SparkSession): Unit = {
    val database = "movielens"
    val timezone = "GMT-6"
    val user = "root"
    val password = ""

    spark.read
      .format("jdbc")
      .option("url", s"jdbc:mysql://127.0.0.1/${database}?serverTimezone=${timezone}")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable",  table)
      .option("user", user)
      .option("password", password)
      .load()
      .write
      .mode(SaveMode.Overwrite) // Other Options: SaveMode.Append, SaveMode.Ignore, SaveMode.ErrorIfExists
      .parquet(s"output/apps/${database}/${table}")
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("Read From MySQL With Joins Example")
      .master("local[*]")
      .getOrCreate()

    val tables = Array("movies", "ratings", "users", "genres", "genres_movies", "occupations")

    tables.foreach( tableName =>
      readSQLTableDumpToParquet(tableName, spark)
    )
  }
}
