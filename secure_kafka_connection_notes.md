```scala
// XXX - WARNING
// This code is trash...
// But the secure connection stuff is more or less correct

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

object ConnectToSecureKafkaDStream {
  case class NestedJsonObject() // Placeholder
  
  case class MyMsg(
                    key_one: Option[String],
                    second_key: Option[Double],
                    something_nested: Option[NestedJsonObject]
                  )
  
  def main(args: Array[String]): Unit = {

      val spark = SparkSession
        .builder
        .appName(“Whatever”)
        .master(“local[*]”) // don’t specify this in prod, pass it in with spark-submit
        .enableHiveSupport()
        .getOrCreate()

      val sparkContext = spark.sparkContext
      val streamingContext = new StreamingContext(sparkContext, Seconds(10))

      val kafkaParams = Map[String, Object] (
        “bootstrap.servers” -> “hostname:9093”,
        “key.deserializer” -> “classOf[StringDeserializer],
        “value.deserializer” -> “classOf[StringDeserializer],
        “group.id“ -> “my-group-name”,
        “auto.offset.reset” -> “earliest”,
        “enable.auto.commit” -> (false: java.lang.Boolean),
        “security.protocol” -> “SSL”,
        “ssl.truststore.location” -> “/path/to/truststore.jks”,
        “ssl.truststore.password” -> sys.env('SOME_ENV_VAR_WITH_PASS')
      )

      val topic = Set(“TOPIC_NAME”)


      val stream = KafkaUtils.createDirectStream[String, String] (
        streamingContext,
        PreferConsistent,
        Subscribe[String, String](topic, kafkaParams)
      )

      val valStream = stream.map(record => record.value)

      val parsedStream = valstream.map(record => parse(record))

      val extractedOneKeyValueStream = parsedStream.map(record => record \ “some_json_key”)

      val extractedObjectStream = valStream.map(record => {
        implicit val formats = Serialization.formats(NoTypeHints)
        parse(record).extract[MyMsg]
      })

      val filteredObjectStream = extractedObjectStream.filter({ record =>
        record.some_key.getOrElse(“BLANK”) == “Some Filter Val”
      })

      val csvTransformedStream = filteredObjectStream.map(record => {
        val someThing = record.some_thing.getOrElse(“some_thing Missing”)
        val someThing2 = record.some_thing2.getOrElse(“some_thing2 Missing”)
        val someThing3 = record.some_thing3.getOrElse(“some_thing3 Missing”)

        val csvRecord = s”$someThing,$someThing2,$someThing3”

        csvRecord

      })

      // Prints 10 records
      csvTransformedStream.print()

      streamingContext.start()
      streamingContext.awaitTermination()
    }
  }
}

```



```bash
source ./secrets_file

ssh -K submit-host-1 \
 SSL_TRUSTSTORE_PASSWORD=$SSL_TRUSTSTORE_PASSWORD \
 KEYTAB_LOCATION=$KEYTAB_LOCATION \
 KERBEROS_PRINCIPAL=$KERBEROS_PRINCIPAL \
 spark2-submit \
 —class spark.MySparkJob \
 —master yarn \
 —deploy-mode client \
 —keytab $KEYTAB_LOCATION \
 —principal $KERBEROS_PRINCIPAL \
 —driver-memory 1G \
 —executor-memory 1G \
 ~/workspace/path/to/my-spark-job-1.0-SNAPSHOT.jar
```

