using Microsoft.AspNetCore.Mvc;
using MongoDBEntitiesMicroservice.Service;

namespace MongoDBEntitiesMicroservice.Controller
{
    [ApiController]
    public class QueryController : ControllerBase
    {
        private readonly IQueryService _service;

        public QueryController(IQueryService service) => _service = service;

        [HttpGet("health")]
        public IActionResult Health() => Ok("OK");

        [HttpGet("a1")]
        public async Task<IActionResult> A1([FromQuery] int repetitions = 10)
            => Ok(await _service.A1(repetitions));

        [HttpGet("a2")]
        public async Task<IActionResult> A2([FromQuery] int repetitions = 10)
            => Ok(await _service.A2(repetitions));

        [HttpGet("a3")]
        public async Task<IActionResult> A3([FromQuery] int repetitions = 10)
            => Ok(await _service.A3(repetitions));

        [HttpGet("a4")]
        public async Task<IActionResult> A4([FromQuery] int repetitions = 10)
            => Ok(await _service.A4(repetitions));

        [HttpGet("b1")]
        public async Task<IActionResult> B1([FromQuery] int repetitions = 10)
            => Ok(await _service.B1(repetitions));

        [HttpGet("b2")]
        public async Task<IActionResult> B2([FromQuery] int repetitions = 10)
            => Ok(await _service.B2(repetitions));

        [HttpGet("c1")]
        public async Task<IActionResult> C1([FromQuery] int repetitions = 10)
            => Ok(await _service.C1(repetitions));

        [HttpGet("c2")]
        public async Task<IActionResult> C2([FromQuery] int repetitions = 10)
            => Ok(await _service.C2(repetitions));

        [HttpGet("c3")]
        public async Task<IActionResult> C3([FromQuery] int repetitions = 10)
            => Ok(await _service.C3(repetitions));

        [HttpGet("c4")]
        public async Task<IActionResult> C4([FromQuery] int repetitions = 10)
            => Ok(await _service.C4(repetitions));

        [HttpGet("c5")]
        public async Task<IActionResult> C5([FromQuery] int repetitions = 10)
            => Ok(await _service.C5(repetitions));

        [HttpGet("d1")]
        public async Task<IActionResult> D1([FromQuery] int repetitions = 10)
            => Ok(await _service.D1(repetitions));

        [HttpGet("d2")]
        public async Task<IActionResult> D2([FromQuery] int repetitions = 10)
            => Ok(await _service.D2(repetitions));

        [HttpGet("d3")]
        public async Task<IActionResult> D3([FromQuery] int repetitions = 10)
            => Ok(await _service.D3(repetitions));

        [HttpGet("e1")]
        public async Task<IActionResult> E1([FromQuery] int repetitions = 10)
            => Ok(await _service.E1(repetitions));

        [HttpGet("e2")]
        public async Task<IActionResult> E2([FromQuery] int repetitions = 10)
            => Ok(await _service.E2(repetitions));

        [HttpGet("e3")]
        public async Task<IActionResult> E3([FromQuery] int repetitions = 10)
            => Ok(await _service.E3(repetitions));

        [HttpGet("q1")]
        public async Task<IActionResult> Q1([FromQuery] int repetitions = 10)
            => Ok(await _service.Q1(repetitions));

        [HttpGet("q2")]
        public async Task<IActionResult> Q2([FromQuery] int repetitions = 10)
            => Ok(await _service.Q2(repetitions));

        [HttpGet("q3")]
        public async Task<IActionResult> Q3([FromQuery] int repetitions = 10)
            => Ok(await _service.Q3(repetitions));

        [HttpGet("q4")]
        public async Task<IActionResult> Q4([FromQuery] int repetitions = 10)
            => Ok(await _service.Q4(repetitions));

        [HttpGet("q5")]
        public async Task<IActionResult> Q5([FromQuery] int repetitions = 10)
            => Ok(await _service.Q5(repetitions));
    }
}
