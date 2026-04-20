import express from 'express';
import { Eureka } from 'eureka-js-client';
import swaggerUi from 'swagger-ui-express';
import { connectDb } from './config/db.js';
import { swaggerSpec } from './config/swagger.js';
import queryRoutes         from './routes/queryRoutes.js';
import embeddedQueryRoutes from './routes/embeddedQueryRoutes.js';
import loaderRoutes        from './routes/loaderRoutes.js';
import { runRelationalLoader } from './loader/loaderR.js';
import { runEmbeddedLoader }   from './loader/loaderE.js';

const PORT        = parseInt(process.env.PORT || '8089');
const LOADER_MODE = process.env.LOADER_MODE === 'true';

async function startLoader() {
    await connectDb();
    console.log('=== LOADER: loading relational collections ===');
    console.log(await runRelationalLoader());
    console.log('=== LOADER: loading embedded collections ===');
    console.log(await runEmbeddedLoader());
    console.log('=== LOADER: finished, exiting ===');
    process.exit(0);
}

async function startService() {
    await connectDb();

    const app = express();
    app.use(express.json());
    app.use('/swagger-ui', swaggerUi.serve, swaggerUi.setup(swaggerSpec));
    app.use('/', queryRoutes);
    app.use('/', embeddedQueryRoutes);
    app.use('/', loaderRoutes);

    app.listen(PORT, () => {
        console.log(`microservice-mongodb-mongoose-javascript running on port ${PORT}`);
        registerWithEureka();
    });
}

function registerWithEureka() {
    const eurekaUrl = process.env.EUREKA_URL;
    if (!eurekaUrl) return;

    // Parse "http://eureka-server:8761/eureka" → host + port
    const match = eurekaUrl.match(/^https?:\/\/([^:\/]+):(\d+)/);
    const eurekaHost = match ? match[1] : 'eureka-server';
    const eurekaPort = match ? parseInt(match[2]) : 8761;
    const hostname   = process.env.HOSTNAME || 'microservice-mongodb-mongoose-javascript';

    const client = new Eureka({
        instance: {
            app: 'microservice-mongodb-mongoose-javascript',
            hostName: hostname,
            ipAddr: hostname,
            port: { '$': PORT, '@enabled': 'true' },
            vipAddress: 'microservice-mongodb-mongoose-javascript',
            dataCenterInfo: {
                '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
                name: 'MyOwn'
            },
            statusPageUrl: `http://${hostname}:${PORT}/health`
        },
        eureka: {
            host: eurekaHost,
            port: eurekaPort,
            servicePath: '/eureka/apps/'
        }
    });

    client.logger.level('warn');
    client.start(err => {
        if (err) console.error('Eureka registration failed:', err.message);
        else     console.log('Registered with Eureka');
    });
}

if (LOADER_MODE) {
    startLoader().catch(err => { console.error(err); process.exit(1); });
} else {
    startService().catch(err => { console.error(err); process.exit(1); });
}
