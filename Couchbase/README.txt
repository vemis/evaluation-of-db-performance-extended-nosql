Commands:

docker run -d --name db_couchbase \
  -p 8091-8096:8091-8096 \
  -p 11210-11211:11210-11211 \
  -v couchbase_data:/opt/couchbase/var \
  couchbase/server:enterprise



docker run -d --name db_couchbase_ottoman \
  -p 8091-8096:8091-8096 \
  -p 11210-11211:11210-11211 \
  -v couchbase_data:/opt/couchbase_ottoman/var \
  couchbase/server:enterprise
  
  
  
docker run -d --name db_couchbase_springdata \
  -p 8091-8096:8091-8096 \
  -p 11210-11211:11210-11211 \
  -v couchbase_data:/opt/couchbase_springdata/var \
  couchbase/server:enterprise
  
