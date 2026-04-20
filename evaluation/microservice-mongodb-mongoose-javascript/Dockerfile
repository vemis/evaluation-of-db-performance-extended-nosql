FROM node:20-alpine

WORKDIR /workspace

# Copy common-js alongside the microservice (package.json references file:../common-js)
COPY common-js/ ./common-js/

# Install common-js dependencies so its imports resolve when Node runs files from that directory
WORKDIR /workspace/common-js
RUN npm install

# Install microservice dependencies (file: reference resolves to ../common-js)
COPY microservice-mongodb-mongoose-javascript/package*.json /workspace/microservice/
WORKDIR /workspace/microservice
RUN npm install

# Copy source
COPY microservice-mongodb-mongoose-javascript/src ./src

# Copy and extract TPC-H dataset
COPY database/tpch-data-small.zip /tmp/tpch-data-small.zip
RUN apk add --no-cache unzip \
    && unzip /tmp/tpch-data-small.zip -d /data \
    && rm /tmp/tpch-data-small.zip

EXPOSE 8089
CMD ["node", "--expose-gc", "src/app.js"]
