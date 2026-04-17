#!/bin/bash
set -e

/entrypoint.sh couchbase-server &
CB_PID=$!

echo "Waiting for Couchbase to start..."
until curl -sf http://localhost:8091/ui/index.html > /dev/null 2>&1; do
  sleep 2
done

CLUSTER_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8091/pools/default)
if [ "$CLUSTER_STATUS" = "404" ]; then
  echo "Initializing cluster 'cluster-main'..."
  couchbase-cli cluster-init \
    -c localhost:8091 \
    --cluster-name "cluster-main" \
    --cluster-username "${COUCHBASE_USER}" \
    --cluster-password "${COUCHBASE_PASSWORD}" \
    --services data,index,query \
    --cluster-ramsize 1024 \
    --cluster-index-ramsize 1024
  echo "Cluster initialized."
else
  echo "Cluster already initialized, skipping."
fi

BUCKET_STATUS=$(curl -s -o /dev/null -w "%{http_code}" \
  -u "${COUCHBASE_USER}:${COUCHBASE_PASSWORD}" \
  http://localhost:8091/pools/default/buckets/bucket-main)
if [ "$BUCKET_STATUS" = "404" ]; then
  echo "Creating bucket 'bucket-main'..."
  couchbase-cli bucket-create \
    -c localhost:8091 \
    -u "${COUCHBASE_USER}" \
    -p "${COUCHBASE_PASSWORD}" \
    --bucket "bucket-main" \
    --bucket-type couchbase \
    --bucket-ramsize 1024
  echo "Bucket created."
else
  echo "Bucket 'bucket-main' already exists, skipping."
fi

wait $CB_PID
