import org.apache.spark.ml.feature.{RegexTokenizer, Tokenizer}
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

val textFilesPath = "/home/john/Dropbox/workspace/spark/spark_cookbook/data/bbc_articles/tech/*"

// --- Read in text and clean it of special characters ---

val df = spark
  .read
  .option("wholetext", true) // Each row stores a file, not a line
  .text(textFilesPath)
  .withColumnRenamed("value", "raw")
  .withColumn("text", $"raw")
  .select($"raw", regexp_replace($"text", "'s", "")                     as "text") // Remove the posessive 's
  .select($"raw", regexp_replace($"text", "[',.!?:;()\\[\\]\\|\"]", "") as "text") // Remove special characters, punctuation
  .select($"raw", regexp_replace($"text", " +-+ +", " ")                as "text") // Remove dashes not used as hyphens

// --- Tokenizing ---

val tokenizer = new Tokenizer()
  .setInputCol("text")
  .setOutputCol("tokens")

val tokenized = tokenizer.transform(df)

// --- Stop Words ---

val remover = new StopWordsRemover()
  .setInputCol("tokens")
  .setOutputCol("stopless_tokens")

val stopless = remover.transform(tokenized)

stopless.show

// scala> stopless.show
//
// +--------------------+--------------------+--------------------+--------------------+
// |                 raw|                text|              tokens|     stopless_tokens|
// +--------------------+--------------------+--------------------+--------------------+
// |Losing yourself i...|Losing yourself i...|[losing, yourself...|[losing, online, ...|
// |Apple laptop is '...|Apple laptop is g...|[apple, laptop, i...|[apple, laptop, g...|
// |Call for action o...|Call for action o...|[call, for, actio...|[call, action, in...|
// |Apple laptop is '...|Apple laptop is g...|[apple, laptop, i...|[apple, laptop, g...|
// |Be careful how yo...|Be careful how yo...|[be, careful, how...|[careful, code, ,...|
// |New consoles prom...|New consoles prom...|[new, consoles, p...|[new, consoles, p...|
// |Rivals of the £40...|Rivals of the £40...|[rivals, of, the,...|[rivals, £400, ap...|
// |What price for 't...|What price for tr...|[what, price, for...|[price, trusted, ...|
// |Podcasts mark ris...|Podcasts mark ris...|[podcasts, mark, ...|[podcasts, mark, ...|
// |'Podcasters' look...|Podcasters look t...|[podcasters, look...|[podcasters, look...|
// |No half measures ...|No half measures ...|[no, half, measur...|[half, measures, ...|
// |When invention tu...|When invention tu...|[when, invention,...|[invention, turns...|
// |Gadget growth fue...|Gadget growth fue...|[gadget, growth, ...|[gadget, growth, ...|
// |Mobile games come...|Mobile games come...|[mobile, games, c...|[mobile, games, c...|
// |Solutions to net ...|Solutions to net ...|[solutions, to, n...|[solutions, net, ...|
// |Who do you think ...|Who do you think ...|[who, do, you, th...|[think, , real, d...|
// |Mobile games come...|Mobile games come...|[mobile, games, c...|[mobile, games, c...|
// |The pirates with ...|The pirates with ...|[the, pirates, wi...|[pirates, profit,...|
// |Mobile networks s...|Mobile networks s...|[mobile, networks...|[mobile, networks...|
// |What high-definit...|What high-definit...|[what, high-defin...|[high-definition,...|
// +--------------------+--------------------+--------------------+--------------------+
// only showing top 20 rows
