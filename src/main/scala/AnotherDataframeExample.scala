import org.apache.spark.sql.SparkSession

object AnotherDataframeExample {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .appName("Another DataFrame App")
      .getOrCreate()

    val df = spark
      .read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("/Users/john/Downloads/Metro_Bike_Share_Trip_Data.csv")

    df.show()
  }
}