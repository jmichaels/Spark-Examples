// kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic nyc_311_requests

import org.apache.spark.sql.SparkSession

object BatchPushSparkDataFrameIntoKafkaExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("Batch Push Spark Data Frame Into Kafka Example App")
      .master("local[*]")
      .getOrCreate()

    val df = spark
      .read
      .parquet("data/nyc_311_requests.parquet")

    // This will convert the dataframe into a single column dataframe,
    // containing a stringified JSON object representing the entire structure
    // of the dataframe
    //
    //    +--------------------+
    //    |               value|
    //    +--------------------+
    //    |{"IncidentType":"...|
    //    |{"IncidentType":"...|
    //    |{"IncidentType":"...|
    //    |{"IncidentType":"...|
    //      ...
    val jsonDf = df.toJSON

    // This will write the entire dataframe out to the specified Kafka topic
    jsonDf.write
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", "nyc_311_requests")
      .save()

    jsonDf.show()
  }
}