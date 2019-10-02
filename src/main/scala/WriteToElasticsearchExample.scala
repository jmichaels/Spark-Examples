import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions
import org.elasticsearch.spark.sql._


object WriteToElasticsearchExample {
  def main(args: Array[String]): Unit = {


    val spark = SparkSession
      .builder
      .appName("Write To Elasticsearch Example App")
      .master("local[*]")
      .config("es.nodes", "http://165.227.182.118")
      .config("es.index.auto.create", "true")
      .config("es.port", "9200")
      .getOrCreate()

    val df = spark
      .read
      .parquet("data/nyc_311_requests.parquet")

    //df.show()

    df.saveToEs("nyc_311_requests")

    val dfReadBackFromEs = spark.esDF("nyc_311_requests")

    dfReadBackFromEs.show
  }
}

