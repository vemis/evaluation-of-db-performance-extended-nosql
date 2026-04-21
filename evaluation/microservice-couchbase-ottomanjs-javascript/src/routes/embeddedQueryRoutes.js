import express from 'express';
import { executeWithMeasurement } from 'common-js';
import * as repo from '../repository/embeddedQueryRepository.js';

const router = express.Router();

router.get('/r1', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.r1(), rep));
});

router.get('/r2', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.r2(), rep));
});

router.get('/r3', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.r3(), rep));
});

router.get('/r4', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.r4(), rep));
});

router.get('/r5', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.r5(), rep));
});

router.get('/r6', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.r6(), rep));
});

router.get('/r7', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.r7(), rep));
});

router.get('/r8', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.r8(), rep));
});

router.get('/r9', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.r9(), rep));
});

export default router;
