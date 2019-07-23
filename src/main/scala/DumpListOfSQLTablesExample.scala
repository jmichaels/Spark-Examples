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

// This demos how to automate reading from a list of tables and
// dumping the contents out to HDFS.
//
// In production you will need to tell spark how to partition the data,
// so you can have more than one Spark executor working on a table.
//
// This will involve telling it which column to partition on (usually `id`)
// and how many partitions you want.
//
// You also may want to repartition/coalesce to a smaller number of partitions
// before writing to parquet, to avoid small files.
//
// Additional Info:
//
// https://medium.com/@radek.strnad/tips-for-using-jdbc-in-apache-spark-sql-396ea7b2e3d3
//
// https://dzone.com/articles/the-right-way-to-use-spark-and-jdbc
//
//   - The code sample from this one can give you some good ideas about how to automate this:
//
//    val primaryKey = executeQuery(url, user, password, s"SHOW KEYS FROM ${config("schema")}.${config("table")} WHERE Key_name = 'PRIMARY'").getString(5)
//    val result = executeQuery(url, user, password, s"select min(${primaryKey}), max(${primaryKey}) from ${config("schema")}.${config("table")}")
//    val min = result.getString(1).toInt
//    val max = result.getString(2).toInt
//    val numPartitions = (max - min) / 5000 + 1
//
//    var df = spark.read.format("jdbc").
//      option("url", s"${url}${config("schema")}").
//      option("driver", "com.mysql.jdbc.Driver").
//      option("lowerBound", min).
//      option("upperBound", max).
//      option("numPartitions", numPartitions).
//      option("partitionColumn", primaryKey).
//      option("dbtable", config("table")).
//      option("user", user).
//      option("password", password).load()
//
//    // some data manipulations here ...
//
//    df.repartition(10).write.mode(SaveMode.Overwrite).parquet(outputPath)



import org.apache.spark.sql.{SaveMode, SparkSession}

object DumpListOfSQLTablesExample {
  def readSQLTableDumpToParquet(table: String, spark: SparkSession): Unit = {
    val database = "movielens"
    val timezone = "GMT-6"
    val user = "root"
    val password = ""

    // This is where you would handle calculating and telling Spark how to
    // partition the data.

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
