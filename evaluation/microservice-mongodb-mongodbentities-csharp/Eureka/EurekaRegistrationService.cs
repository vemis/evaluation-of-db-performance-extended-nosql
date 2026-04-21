using System.Text;

namespace MongoDBEntitiesMicroservice.Eureka
{
    /// <summary>
    /// Registers this service with Spring Cloud Eureka via REST API and sends periodic heartbeats.
    /// Mirrors the pattern used by the JS services (eureka-js-client).
    /// </summary>
    public class EurekaRegistrationService : BackgroundService
    {
        private const string AppName = "microservice-mongodb-mongodbentities-csharp";
        private const int Port = 8097;

        private readonly ILogger<EurekaRegistrationService> _logger;
        private readonly string? _eurekaUrl;
        private string? _instanceId;

        public EurekaRegistrationService(IConfiguration config, ILogger<EurekaRegistrationService> logger)
        {
            _logger = logger;
            _eurekaUrl = config["EUREKA_URL"];
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            if (string.IsNullOrEmpty(_eurekaUrl))
            {
                _logger.LogInformation("EUREKA_URL not set, skipping Eureka registration.");
                return;
            }

            var hostname = Environment.GetEnvironmentVariable("HOSTNAME") ?? AppName;
            _instanceId = $"{hostname}:{AppName}:{Port}";

            await RegisterAsync(hostname, stoppingToken);

            // Send heartbeat every 30 seconds
            using var timer = new PeriodicTimer(TimeSpan.FromSeconds(30));
            while (!stoppingToken.IsCancellationRequested && await timer.WaitForNextTickAsync(stoppingToken))
            {
                await SendHeartbeatAsync(stoppingToken);
            }
        }

        private async Task RegisterAsync(string hostname, CancellationToken ct)
        {
            try
            {
                // Build JSON manually to use "$" and "@class" keys that C# anonymous types cannot express
                var json = $$"""
                {
                  "instance": {
                    "instanceId": "{{_instanceId}}",
                    "app": "{{AppName.ToUpper()}}",
                    "hostName": "{{hostname}}",
                    "ipAddr": "{{hostname}}",
                    "status": "UP",
                    "port": { "$": {{Port}}, "@enabled": "true" },
                    "securePort": { "$": 443, "@enabled": "false" },
                    "vipAddress": "{{AppName}}",
                    "secureVipAddress": "{{AppName}}",
                    "healthCheckUrl": "http://{{hostname}}:{{Port}}/health",
                    "statusPageUrl": "http://{{hostname}}:{{Port}}/health",
                    "homePageUrl": "http://{{hostname}}:{{Port}}/",
                    "dataCenterInfo": {
                      "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                      "name": "MyOwn"
                    }
                  }
                }
                """;

                var url = $"{_eurekaUrl!.TrimEnd('/')}/apps/{AppName}";

                using var client = new HttpClient();
                var response = await client.PostAsync(url,
                    new StringContent(json, Encoding.UTF8, "application/json"), ct);

                if (response.IsSuccessStatusCode)
                    _logger.LogInformation("Registered with Eureka as {AppName}", AppName);
                else
                    _logger.LogWarning("Eureka registration returned {Status}", response.StatusCode);
            }
            catch (Exception ex)
            {
                _logger.LogWarning("Eureka registration failed: {Message}", ex.Message);
            }
        }

        private async Task SendHeartbeatAsync(CancellationToken ct)
        {
            try
            {
                var url = $"{_eurekaUrl!.TrimEnd('/')}/apps/{AppName}/{_instanceId}";
                using var client = new HttpClient();
                await client.PutAsync(url, null, ct);
            }
            catch (Exception ex)
            {
                _logger.LogWarning("Eureka heartbeat failed: {Message}", ex.Message);
            }
        }

        public override async Task StopAsync(CancellationToken ct)
        {
            if (!string.IsNullOrEmpty(_eurekaUrl) && _instanceId != null)
            {
                try
                {
                    var url = $"{_eurekaUrl.TrimEnd('/')}/apps/{AppName}/{_instanceId}";
                    using var client = new HttpClient();
                    await client.DeleteAsync(url, ct);
                }
                catch { /* best-effort deregistration */ }
            }
            await base.StopAsync(ct);
        }
    }
}
