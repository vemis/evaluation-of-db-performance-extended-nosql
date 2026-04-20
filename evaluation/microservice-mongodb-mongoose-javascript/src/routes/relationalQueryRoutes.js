import express from 'express';
import { executeWithMeasurement } from 'common-js';
import * as repo from '../repository/relationalQueryRepository.js';

const router = express.Router();

router.get('/health', (_req, res) => res.send('OK'));

router.get('/a1', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.a1(), rep));
});

router.get('/a2', async (req, res) => {
    const rep       = parseInt(req.query.repetitions) || 10;
    const startDate = new Date(req.query.startDate || '1996-01-01');
    const endDate   = new Date(req.query.endDate   || '1996-12-31');
    res.json(await executeWithMeasurement(() => repo.a2(startDate, endDate), rep));
});

router.get('/a3', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.a3(), rep));
});

router.get('/a4', async (req, res) => {
    const rep         = parseInt(req.query.repetitions) || 10;
    const minOrderKey = parseInt(req.query.minOrderKey) || 1000;
    const maxOrderKey = parseInt(req.query.maxOrderKey) || 50000;
    res.json(await executeWithMeasurement(() => repo.a4(minOrderKey, maxOrderKey), rep));
});

router.get('/b1', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.b1(), rep));
});

router.get('/b2', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.b2(), rep));
});

router.get('/c1', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.c1(), rep));
});

router.get('/c2', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.c2(), rep));
});

router.get('/c3', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.c3(), rep));
});

router.get('/c4', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.c4(), rep));
});

router.get('/c5', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.c5(), rep));
});

router.get('/d1', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.d1(), rep));
});

router.get('/d2', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.d2(), rep));
});

router.get('/d3', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.d3(), rep));
});

router.get('/e1', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.e1(), rep));
});

router.get('/e2', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.e2(), rep));
});

router.get('/e3', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.e3(), rep));
});

router.get('/q1', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.q1(), rep));
});

router.get('/q2', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.q2(), rep));
});

router.get('/q3', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.q3(), rep));
});

router.get('/q4', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.q4(), rep));
});

router.get('/q5', async (req, res) => {
    const rep = parseInt(req.query.repetitions) || 10;
    res.json(await executeWithMeasurement(() => repo.q5(), rep));
});

export default router;
