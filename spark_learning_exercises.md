
# Spark Learning Exercises

## The Data

**NYC's 311 Service Requests - 2010 to Present** 

Data download location:

https://data.cityofnewyork.us/resource/fhrw-4uyv.json

Data source info:

https://data.cityofnewyork.us/Social-Services/311-Service-Requests-from-2010-to-Present/7ahn-ypff

What it looks like:

```json
{  
  "unique_key":"28428727",
  "created_date":"2014-07-08T10:45:03.000",
  "closed_date":"2014-07-16T08:05:45.000",
  "agency":"DPR",
  "agency_name":"Department of Parks and Recreation",
  "complaint_type":"Damaged Tree",
  "descriptor":"Branch or Limb Has Fallen Down",
  "location_type":"Street",
  "incident_zip":"11365",
  "incident_address":"LITHONIA AVENUE",
  "street_name":"LITHONIA AVENUE",
  "cross_street_1":"168 STREET",
  "cross_street_2":"169 STREET",
  "address_type":"BLOCKFACE",
  "city":"FRESH MEADOWS",
  "facility_type":"N/A",
  "status":"Closed",
  "due_date":"2014-07-16T10:45:03.000",
  "resolution_description":"The Department of Parks and Recreation inspected but the condition was not found.",
  "resolution_action_updated_date":"2014-07-16T08:05:45.000",
  "community_board":"07 QUEENS",
  "borough":"QUEENS",
  "x_coordinate_state_plane":"1039823",
  "y_coordinate_state_plane":"212155",
  "open_data_channel_type":"PHONE",
  "park_facility_name":"Unspecified",
  "park_borough":"QUEENS",
  "latitude":"40.74881840234017",
  "longitude":"-73.79943159765267",
  "location":{  
	"type":"Point",
	"coordinates":[  
	  -73.799431597653,
	  40.74881840234
	 ]
  },
  ":@computed_region_efsh_h5xi":"14507",
  ":@computed_region_f5dn_yrer":"22",
  ":@computed_region_yeji_bk3q":"3",
  ":@computed_region_92fq_4b7q":"3",
  ":@computed_region_sbqj_enih":"67"
}
```

## Setup

Download this JSON file:

`wget https://data.cityofnewyork.us/resource/fhrw-4uyv.json`

It's a small subset of the full dataset, but will work well for practicing Spark.

When you're comfortable with the small dataset, you can try running the queries on the full dataset.
 
Start the spark shell: `spark-shell`

## The Exercises

Load the data into a DataFrame, using the "inferSchema" option.  

```scala

val df = spark.read.option("multiline", true).option("inferSchema", true).json("/path/to/the/json/file.json")

```

  The `.option("multiline", true)` tells spark that the JSON file you're trying to read is actually an Array of JSON objects, instead of a file containing completely separate, new-line delineated objects.  If you look at the file, you'll see that it starts with a `[` and ends with a `]`.

  Try reading it without the `multiline` option.  What happens?

Use `df.show` to print 20 rows.

Use `df.show(100)` to print out 100 rows.

Use `df.show(false)` to print out 20 rows, without truncating the length of the values.

Use `df.show(50, 10)` to print out 50 rows, truncating the lenth of the values to 10 characters. 

Print out the schema using `df.printSchema`.  Did Spark correctly guess the types for all the columns?  Or are "date" columns listed as "string" type?

Try reading in the JSON file again without the `inferSchema` option, then look at the schema using `.printSchema`.  Notice the difference?

Get a `count` of the number of rows in the DataFrame.

Use the `distinct` method to get a list of all the unique values in the "Complaint Type" column

```scala
df.select("complaint_type").distinct
```

Now, order them alphabetically

```scala
df.select("complaint_type").distinct.orderBy(asc("complaint_type"))
```

Use the `desc` method to sort them in reverse alphabetical order

How many complaints are about the "Sewer"?

```scala
df.filter("complaint_type = 'Sewer'").count
```

How many requests came from the `borough` of `QUEENS`?

Let's see exactly what people are complaining about, and how their complaints were resolved.  Create a new DataFrame containing only the "descriptor" and "resolution_descriptor" columns.

```scala
df.select("descriptor", "resolution_description").show
```

Add a column to the DataFrame, where everyone is shouting:

```scala
df.select(upper($"descriptor"), upper($"resolution_description")).show
```

Those `$`s converts $"col name" into an Column object.  Some methods accept column names as strings, others require column objects.

Let's combine the `descriptor` and `resolution_description` columns' content into a new column and add it to the DataFrame.

```scala
df.select("descriptor", "resolution_description").withColumn("combined_description", $"descriptor" + $"resolution_description").show
```

Which zip code produced the most requests?

```scala
df.groupBy("incident_zip").count.orderBy(desc("count")).show
```

Which street produced the most requests?  Which street produced the least?

Let's do some date formatting.  This is a bit tedious, but it will let us do some really interesting analytics, such as viewing trends over time and looking for spikes.

First, create a correctly formatted date column for the `created_date`, and add it to the DataFrame.

Let's look at it by itself:

```scala

df.select("created_date").show(false)

// +-----------------------+
// |created_date           |
// +-----------------------+
// |2016-12-20T09:51:00.000|
// |2016-12-20T20:27:58.000|
// |2016-12-20T10:35:36.000|
// |2016-12-20T12:20:04.000|

```

Now let's fix the timestamp:

```scala

df.select("created_date").withColumn("created_date_fixed", to_timestamp($"created_date", "yyyy-MM-dd'T'HH:mm:ss.SSS")).show(false)

```

Now let's replace the incorrectly parsed one on the original dataframe:

```scala

df.withColumn("created_date", to_timestamp($"created_date", "yyyy-MM-dd'T'HH:mm:ss.SSS"))

```

Once you have a correctly formatted timestamp, you can do things like this:

```scala

df.withColumn("created_date", to_timestamp($"created_date", "yyyy-MM-dd'T'HH:mm:ss.SSS"))
  .withColumn("month", month($"created_date"))
  .groupBy("month")
  .count
  .show

```

Which day of the month had the most requests?  Which year?  (The small dataset will probably only have one year)
