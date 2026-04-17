FROM couchbase:enterprise-7.6.2

COPY database/couchbase/configure-cluster.sh /configure-cluster.sh
RUN chmod +x /configure-cluster.sh

EXPOSE 8091 8092 8093 8094 8095 8096 11210 11211

ENTRYPOINT ["/configure-cluster.sh"]
