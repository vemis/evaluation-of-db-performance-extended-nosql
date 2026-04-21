using Microsoft.AspNetCore.Mvc;
using MongoDBEntitiesMicroservice.Loader;

namespace MongoDBEntitiesMicroservice.Controller
{
    [ApiController]
    public class LoaderController : ControllerBase
    {
        private readonly string _dataPath;

        public LoaderController(IConfiguration config)
        {
            _dataPath = config["TPCH_DATA_PATH"] ?? "/data/tpch-data-small";
        }

        [HttpPost("load")]
        public async Task<IActionResult> Load()
        {
            var result = await LoaderR.Run(_dataPath);
            return Ok(result);
        }
    }
}
