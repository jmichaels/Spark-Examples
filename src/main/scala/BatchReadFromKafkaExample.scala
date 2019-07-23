import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions

object BatchReadFromKafkaExample {
  def main(args: Array[String]): Unit = {


    val spark = SparkSession
      .builder
      .appName("Batch Push Spark Data Frame Into Kafka Example App")
      .master("local[*]")
      .getOrCreate()

    val nyc311RequestSchema = spark.read.parquet("data/nyc_311_requests.parquet").schema

    println(nyc311RequestSchema)

    // By default, this will read from the earliest offset to the most recent
    val df = spark
      .read
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "nyc_311_requests")
      .load()

    df.show()
    // df looks like this:
    //
    //    +----+--------------------+----------------+---------+------+--------------------+-------------+
    //    | key|               value|           topic|partition|offset|           timestamp|timestampType|
    //    +----+--------------------+----------------+---------+------+--------------------+-------------+
    //    |null|[7B 22 49 6E 63 6...|nyc_311_requests|        8|     0|2019-06-19 22:23:...|            0|
    //    |null|[7B 22 49 6E 63 6...|nyc_311_requests|        8|     1|2019-06-19 22:23:...|            0|
    //    |null|[7B 22 49 6E 63 6...|nyc_311_requests|        8|     2|2019-06-19 22:23:...|            0|
    //    |null|[7B 22 49 6E 63 6...|nyc_311_requests|        8|     3|2019-06-19 22:23:...|            0|

    // We need to cast the value as a String, and


    println(df.count)

    // 8326

    // Let's look at how the messages were distributed among partitions:
    df.groupBy("partition").count().show()

    //    +---------+-----+
    //    |partition|count|
    //    +---------+-----+
    //    |        1|  832|
    //    |        6|  832|
    //    |        3|  832|
    //    |        5|  833|
    //    |        9|  833|
    //    |        4|  833|
    //    |        8|  833|
    //    |        7|  832|
    //    |        2|  833|
    //    |        0|  833|
    //    +---------+-----+


    // XXX - THIS ISN'T WORKING YET - XXX
    // ----------------------------------

    val deserializedDf = df.selectExpr("CAST(value AS STRING)")
      //.withColumn("parsed_message", functions.from_json(df("value"), nyc311RequestSchema))
    deserializedDf.show()
    //    +--------------------+
    //    |               value|
    //    +--------------------+
    //    |{"IncidentType":"...|
    //    |{"IncidentType":"...|
    //    |{"IncidentType":"...|
    //    |{"IncidentType":"...|
    //    |{"IncidentType":"...|




              val deserializedDf2 = df.select(functions.from_json(df("value").cast("string"), nyc311RequestSchema))
    deserializedDf2.show(false)
    //    +------------------------------------------------------------------------------------------------------------------------------------------------+
    //    |jsontostructs(CAST(value AS STRING))                                                                                                            |
    //    +------------------------------------------------------------------------------------------------------------------------------------------------+
    //    |[Utility-Other,, Manhattan,,, 2016-11-22 08:53:17,]                                                                                             |
    //    |[Structural-Collapse, 55 Thompson St, Manhattan, 40.71442154062271, -74.00607638041981, 2016-11-28 21:51:55,]                                   |
    //    |[Structural-Crane, 229 Cherry Street, Manhattan, 40.71442154062271, -74.00607638041981, 2016-11-30 16:29:12,]                                   |
    //    |[Transportation-Other,, Queens, 40.74419565512141, -73.77154423626095, 2017-01-19 04:08:25,]                                                    |
    //    |[Utility-Gas Low Pressure, Noble Avenue & Watson Avenue, Bronx, 40.82730091310776, -73.86917897682753, 2017-01-30 21:47:09,]                    |
    //    |[Fire-1st Alarm, 150-05 Cohancy Street, Queens, 40.71400364095638, -73.82998933154158, 2016-11-20 13:03:30,]                                    |
    //    |[Utility-Water Service Line,, Bronx,,, 2017-07-11 04:50:50,]                                                                                    |




    val deserializedDf3 = df.select(df("value").cast("string"))
    deserializedDf3.show()
    //    +--------------------+
    //    |               value|
    //    +--------------------+
    //    |{"IncidentType":"...|
    //    |{"IncidentType":"...|
    //    |{"IncidentType":"...|


  }
}

