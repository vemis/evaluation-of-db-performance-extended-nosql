using System.Globalization;
using MongoDB.Driver;
using MongoDB.Entities;
using MongoDBEntitiesMicroservice.Eureka;
using MongoDBEntitiesMicroservice.Loader;
using MongoDBEntitiesMicroservice.Repository;
using MongoDBEntitiesMicroservice.Service;

// Invariant culture — required for TPC-H double parsing (e.g. "16473.51" uses dots)
CultureInfo.DefaultThreadCurrentCulture = CultureInfo.InvariantCulture;
CultureInfo.DefaultThreadCurrentUICulture = CultureInfo.InvariantCulture;

var mongoUri = Environment.GetEnvironmentVariable("MONGODB_URI") ?? "mongodb://localhost:27017";
var mongoDatabase = Environment.GetEnvironmentVariable("MONGODB_DATABASE") ?? "mongodbentities_tpch";
var dataPath = Environment.GetEnvironmentVariable("TPCH_DATA_PATH") ?? "/data/tpch-data-small";
var loaderMode = Environment.GetEnvironmentVariable("LOADER_MODE") == "true";

Console.WriteLine($"Connecting to MongoDB: {mongoUri}, database: {mongoDatabase}");
await DB.InitAsync(mongoDatabase, MongoClientSettings.FromConnectionString(mongoUri));
Console.WriteLine("MongoDB.Entities initialized.");

if (loaderMode)
{
    Console.WriteLine("=== LOADER MODE: loading relational collections ===");
    Console.WriteLine(await LoaderR.Run(dataPath));
    Console.WriteLine("=== LOADER MODE: loading embedded collections ===");
    Console.WriteLine(await LoaderE.Run(dataPath));
    Console.WriteLine("=== LOADER MODE: finished, exiting ===");
    return;
}

var builder = WebApplication.CreateBuilder(args);

// Bind port 8097
builder.WebHost.UseUrls("http://0.0.0.0:8097");

// Make env vars available via IConfiguration
builder.Configuration.AddEnvironmentVariables();

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new() { Title = "microservice-mongodb-mongodbentities-csharp", Version = "v1" });
});

builder.Services.AddScoped<IRelationalQueryRepository, RelationalQueryRepository>();
builder.Services.AddScoped<IEmbeddedQueryRepository, EmbeddedQueryRepository>();
builder.Services.AddScoped<IQueryService, QueryService>();
builder.Services.AddScoped<IEmbeddedQueryService, EmbeddedQueryService>();
builder.Services.AddHostedService<EurekaRegistrationService>();

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI(c => c.SwaggerEndpoint("/swagger/v1/swagger.json", "MongoDBEntities C# v1"));

app.MapControllers();

Console.WriteLine("microservice-mongodb-mongodbentities-csharp running on port 8097");
app.Run();
