import express from 'express';
import { runRelationalLoader } from '../loader/loaderR.js';
import { runEmbeddedLoader }   from '../loader/loaderE.js';

const router = express.Router();

router.post('/load', async (_req, res) => {
    const msg = await runRelationalLoader();
    res.send(msg);
});

router.post('/loadEmbedded', async (_req, res) => {
    const msg = await runEmbeddedLoader();
    res.send(msg);
});

export default router;
