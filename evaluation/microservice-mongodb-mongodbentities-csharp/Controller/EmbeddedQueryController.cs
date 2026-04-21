using Microsoft.AspNetCore.Mvc;
using MongoDBEntitiesMicroservice.Service;

namespace MongoDBEntitiesMicroservice.Controller
{
    [ApiController]
    public class EmbeddedQueryController : ControllerBase
    {
        private readonly IEmbeddedQueryService _service;

        public EmbeddedQueryController(IEmbeddedQueryService service) => _service = service;

        [HttpGet("r1")]
        public async Task<IActionResult> R1([FromQuery] int repetitions = 10)
            => Ok(await _service.R1(repetitions));

        [HttpGet("r2")]
        public async Task<IActionResult> R2([FromQuery] int repetitions = 10)
            => Ok(await _service.R2(repetitions));

        [HttpGet("r3")]
        public async Task<IActionResult> R3([FromQuery] int repetitions = 10)
            => Ok(await _service.R3(repetitions));

        [HttpGet("r4")]
        public async Task<IActionResult> R4([FromQuery] int repetitions = 10)
            => Ok(await _service.R4(repetitions));

        [HttpGet("r5")]
        public async Task<IActionResult> R5([FromQuery] int repetitions = 10)
            => Ok(await _service.R5(repetitions));

        [HttpGet("r6")]
        public async Task<IActionResult> R6([FromQuery] int repetitions = 10)
            => Ok(await _service.R6(repetitions));

        [HttpGet("r7")]
        public async Task<IActionResult> R7([FromQuery] int repetitions = 10)
            => Ok(await _service.R7(repetitions));

        [HttpGet("r8")]
        public async Task<IActionResult> R8([FromQuery] int repetitions = 10)
            => Ok(await _service.R8(repetitions));

        [HttpGet("r9")]
        public async Task<IActionResult> R9([FromQuery] int repetitions = 10)
            => Ok(await _service.R9(repetitions));
    }
}
