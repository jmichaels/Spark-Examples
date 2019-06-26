import org.apache.spark.sql.SparkSession

object RunOnRemoteYARNClusterFromIntelliJExample {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .config("spark.hadoop.yarn.resourcemanager.hostname", "jmichaels-1.gce.cloudera.com")
      .config("spark.hadoop.yarn.resourcemanager.address", "jmichaels-1.gce.cloudera.com:8032")
      .config("spark.hadoop.yarn.namenodes", "hdfs://jmichaels-1.gce.cloudera.com:8020,hdfs://jmichaels-1.gce.cloudera.com:8020")
      .config("spark.yarn.stagingDir", "hdfs://jmichaels-1.gce.cloudera.com:8020/user/john")
      .config("spark.yarn.jars", "hdfs://jmichaels-1.gce.cloudera.com:8020/user/spark/jars/*")
      .appName("Another DataFrame App")
      .master("yarn")
      .getOrCreate()


    val df = spark
      .read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("/User/john/Downloads/Metro_Bike_Share_Trip_Data.csv")

    df.show()
  }
}