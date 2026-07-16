# evaluation-of-db-performance

This repository holds the implementation for the bachelor thesis *Comparison of Performance Characteristics of ODM Frameworks over Document Database Systems* (Michal Šindler, Charles University, MFF, 2026).

It is a microservice-based benchmarking system that measures the execution time and allocated memory of database access frameworks running the same TPC-H–based workload. Each framework runs in its own microservice; an orchestrator fans out the benchmark requests, and a web GUI lets you configure runs and visualise the results directly in the browser.

The system covers **12 frameworks — 6 ODM and 6 ORM**:

- 6 **ODM** (object-document mapping) frameworks over MongoDB and Couchbase, in Java, C#, and JavaScript/Node.js: Morphia, Spring Data MongoDB, Mongoose.js, MongoDB.Entities (C#), Spring Data Couchbase, and Ottoman.js.
- 6 **ORM** (object-relational mapping) frameworks over MySQL, in Java: JDBC, Spring Data JPA, MyBatis, jOOQ, Ebean, and Apache Cayenne.

## Repository Structure

The repository is split into two independent parts:

- **`bachelor-thesis-odms/`** — a collection of standalone ODM sample projects, organised by database (`MongoDB/`, `Couchbase/`) and then by language (`JavaODMs/`, `CSharpODMs/`, `JavaScript-Node.jsODMs/`). These serve as the **static comparison**: small, self-contained projects that illustrate how each ODM framework models data and expresses queries, independent of the benchmark.
- **`evaluation/`** — the **benchmarking application** itself: the 12 framework microservices, the orchestrator, the Eureka service registry, the database setup, and the React frontend, wired together via Docker Compose. This is what you run to measure and compare performance (see [How to Use](#how-to-use) below).

## How to Use

The whole platform is containerised, so the only software you need on the host is **Docker** together with the **Docker Compose** plugin. Every other dependency — the language runtimes (JDK, Node.js, .NET SDK), the database servers (MongoDB, Couchbase, MySQL), the service registry, the orchestrator, and the frontend — is provided by the images built from `docker-compose.yml`.

### Requirements

- **Docker** and the **Docker Compose** plugin.
- **32 GB RAM** is the comfortable operating point. The stack also runs on a machine with **16 GB RAM**, but there the loader containers must be started *sequentially* rather than all at once — bringing the whole stack up in a single `docker compose up` spikes memory usage above the 16 GB threshold (see [Note for machines with 16 GB RAM](#note-for-machines-with-16-gb-ram)).
- All host ports bound by the containers must be free before starting: `3000` (frontend), `3306` (MySQL), `8080`–`8097` (query microservices and Couchbase management interfaces), `8100` (orchestrator), `8761` (Eureka), `11210`–`11211` (Couchbase data service), and `27017` (MongoDB).

### 1. Obtain the platform

```bash
git clone https://github.com/vemis/evaluation-of-db-performance-extended-nosql.git
cd evaluation-of-db-performance-extended-nosql/evaluation
```

The TPC-H benchmark data is bundled with the source as `database/tpch-data-small.zip` and is unpacked into the images at build time, so no separate data-generation step is necessary.

### 2. Configure credentials

Database credentials are read from an `.env` file that is not part of the repository. A template with sensible defaults is provided — copying it is enough to obtain a working configuration:

```bash
cp .env.template .env
```

The file defines the MySQL and Couchbase accounts used by the databases and the microservices that connect to them:

```bash
# MYSQL
MYSQL_USER=admin
MYSQL_ROOT_PASSWORD=password
MYSQL_PASSWORD=password
MYSQL_DATABASE=db

# COUCHBASE
COUCHBASE_USER=Administrator
COUCHBASE_PASSWORD=password
```

The default values are adequate for a local, isolated benchmarking run; change them if the platform is exposed beyond the local machine. No MongoDB credentials are needed — MongoDB is started without access control and is reachable only inside the Docker network (plus a localhost port mapping).

### 3. Build and start the stack

With the configuration in place, the entire stack — the three databases, the Eureka service registry, the per-framework microservices, the orchestrator, and the frontend — is built and started with a single command:

```bash
docker compose up --build
```

Depending on the Docker version installed on the host, the equivalent hyphenated command may be required instead:

```bash
docker-compose up --build
```

Compose resolves the startup order through the declared dependencies. Each document store is brought up first and, once healthy, its data is populated by a dedicated *loader* container that runs exactly once and then exits; the corresponding query microservice only starts after its loader has completed successfully. The microservices register themselves with Eureka on startup, which is how the orchestrator later discovers them.

Once the stack is up, the following endpoints are exposed on the host:

- Eureka dashboard — <http://localhost:8761>
- Orchestrator REST API — <http://localhost:8100>
- **Frontend — <http://localhost:3000>**

#### Note for machines with 16 GB RAM

Loading all stores at once exceeds the available memory budget on a 16 GB machine. In that case, start the loaders sequentially by hand rather than bringing the whole stack up through a single `docker compose up`.

### 4. Execute benchmarks and inspect results

Benchmarks are driven entirely from the web frontend at <http://localhost:3000> — no manual calls to the orchestrator API are required.

1. **Compose a run.** Pick a query from the *Select Query* dropdown. This enables the checkboxes of the frameworks that support it (frameworks that do not implement the selected query stay disabled) and shows a preview of the underlying query below the form. Set *Repetitions* to control how many times each framework executes the query, and use *Check all* to select every enabled framework at once.
2. **Execute.** Pressing *Execute* sends the query, repetition count, and framework set to the orchestrator, which discovers the corresponding microservices through Eureka, invokes each of them the requested number of times, and records the execution time and memory usage of every iteration.
3. **Read the results.** The results table lists, per framework, the number of repetitions together with the average, minimum, and maximum execution time (in milliseconds) and memory usage (in bytes); each row can be expanded to reveal the individual per-iteration measurements. The same figures are visualised below as comparative bar charts. *Export CSV* writes the raw results to a CSV file for further processing, and *Home* returns to the run form.
