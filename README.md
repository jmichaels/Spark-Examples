# Maven / Scala / Spark - IntelliJ Project Setup
* Install the Scala plugin for IntelliJ
* Create a new blank Maven project
	* No archetype
* Go to the spark examples repo and copy the dependencies portion of their pom.xml into your pom.xml
* -Edit it so it looks like this- (just copy this…)

```xml

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.your-org-here</groupId>
    <artifactId>maven-intellij-project-setup</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <scalaVersion>2.11</scalaVersion>
        <sparkVersion>2.3.3</sparkVersion>
    </properties>

    <!-- Change 'compile' to 'provided' if the Jar is available on your Spark cluster -->
    <dependencies>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_${scalaVersion}</artifactId>
            <version>${sparkVersion}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-streaming_${scalaVersion}</artifactId>
            <version>${sparkVersion}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-mllib_${scalaVersion}</artifactId>
            <version>${sparkVersion}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-hive_${scalaVersion}</artifactId>
            <version>${sparkVersion}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-graphx_${scalaVersion}</artifactId>
            <version>${sparkVersion}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-streaming-kafka-0-10_${scalaVersion}</artifactId>
            <version>${sparkVersion}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql-kafka-0-10_${scalaVersion}</artifactId>
            <version>${sparkVersion}</version>
            <scope>compile</scope>
        </dependency>

    </dependencies>

    <build>
        <sourceDirectory>src/main/scala</sourceDirectory>
        <testSourceDirectory>src/test/scala</testSourceDirectory>

        <plugins>
            <plugin>
                <groupId>net.alchim31.maven</groupId>
                <artifactId>scala-maven-plugin</artifactId>
                <version>4.0.2</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>testCompile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

        </plugins>
    </build>
</project>


```

* Create a `scala` folder under `main` folder
* Enable `scala` framework support
	* Right click on the top level project directory
	* Click “add framework support”

# Create your first Spark app
* Right click on `scala` folder
	* New -> Scala Class
		* Name: “Simple DataFrame Example”
		* Kind: “Scala Object”
* Put this code in the file, replacing any generated code:

```scala

import org.apache.spark.sql.SparkSession

object SimpleDataframeExample {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .appName("Simple Data Frame Example")
		.master("local[*]")
      .getOrCreate()

    val df = spark
      .read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("/path/to/some_arbitrary.csv")

    df.printSchema()
  }
}

```

* Replace `/path/to/some_arbitrary.csv` with any valid CSV

# Resolve dependencies
`mvn dependency:resolve`

# Run It
* Click on the green triangle in the margin to the left of your `main` method declaration
* Click `RunSimpleDataframeExample`

# Package it into a JAR
```bash

cd project-directory
mvn compile package

```

# `spark-submit` it to the cluster
```bash

spark-submit --class SimpleDataframeExample target/maven-intellij-project-setup-1.0-SNAPSHOT.jar

```

# Building/submitting multiple applications in one project
* Create another Scala object file
* Name it `AnotherDataframeExample`
* Put this code into it:

```scala

import org.apache.spark.sql.SparkSession

object AnotherDataframeExample {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .appName("Data Frame Examples App")
		.master("local[*]")
      .getOrCreate()

    val df = spark
      .read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("/path/to/some_arbitrary.csv")

    df.show()
  }
}

```

* Instead of printing the schema, this will show 20 rows
* Run it, by clicking the green triangle next to your `main` method, to make sure it works
* Package the project again:

```bash

cd project-directory
mvn compile package

```

# `spark-submit` the JAR with both Spark Apps, telling Spark to run the new app
```bash

spark-submit --class AnotherDataframeExample target/maven-intellij-project-setup-1.0-SNAPSHOT.jar

```
