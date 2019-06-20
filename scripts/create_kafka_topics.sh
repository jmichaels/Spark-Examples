kafka-topics \
  --create \
  --partitions 10 \
  --replication-factor 1 \
  --topic nyc_311_requests \
  --zookeeper localhost

kafka-topics \
  --create \
  --partitions 10 \
  --replication-factor 1 \
  --topic bike_share_records \
  --zookeeper localhost