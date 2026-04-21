using Microsoft.AspNetCore.Mvc;
using MongoDBEntitiesMicroservice.Loader;

namespace MongoDBEntitiesMicroservice.Controller
{
    [ApiController]
    public class EmbeddedLoaderController : ControllerBase
    {
        private readonly string _dataPath;

        public EmbeddedLoaderController(IConfiguration config)
        {
            _dataPath = config["TPCH_DATA_PATH"] ?? "/data/tpch-data-small";
        }

        [HttpPost("loadEmbedded")]
        public async Task<IActionResult> LoadEmbedded()
        {
            var result = await LoaderE.Run(_dataPath);
            return Ok(result);
        }
    }
}
