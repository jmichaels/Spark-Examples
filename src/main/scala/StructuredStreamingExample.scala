import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import scala.concurrent.duration._

// This is an excellent tutorial on Structured Streaming w/Kafka
//
// https://aseigneurin.github.io/2018/08/14/kafka-tutorial-8-spark-structured-streaming.html


object StructuredStreamingExample {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("Structured Streaming Example App")
      .master("local[*]")
      .getOrCreate()


    val inputStream = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "bike_share_record")
      .option("startingOffsets", "earliest")
      .load()

    val consoleOutput = inputStream
      .writeStream
      .format("console")
      .trigger(Trigger.ProcessingTime(10.seconds))
      .start()

    consoleOutput.awaitTermination()

  }
}
