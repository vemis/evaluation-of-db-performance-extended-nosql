FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copy project files for restore (layer cache)
COPY common-csharp/common-csharp.csproj ./common-csharp/
COPY microservice-mongodb-mongodbentities-csharp/microservice-mongodb-mongodbentities-csharp.csproj ./microservice/
RUN dotnet restore ./microservice/microservice-mongodb-mongodbentities-csharp.csproj

# Copy source
COPY common-csharp/ ./common-csharp/
COPY microservice-mongodb-mongodbentities-csharp/ ./microservice/

RUN dotnet publish ./microservice/microservice-mongodb-mongodbentities-csharp.csproj \
    -c Release -o /app/publish --no-restore

FROM mcr.microsoft.com/dotnet/aspnet:8.0
WORKDIR /app

COPY --from=build /app/publish .

# Extract TPC-H dataset
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*
COPY database/tpch-data-small.zip /tmp/tpch-data-small.zip
RUN unzip /tmp/tpch-data-small.zip -d /data && rm /tmp/tpch-data-small.zip

EXPOSE 8097
ENTRYPOINT ["dotnet", "microservice-mongodb-mongodbentities-csharp.dll"]
