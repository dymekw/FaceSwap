package com.faceswap.controller;

import static com.faceswap.Utils.STATIC_IMAGE;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.faceswap.view.listeners.ProgressListener;

public class SwapService {

	private static final Integer THREAD_POOL_SIZE = 10;
	private static final Integer FRAMES_PER_WORKER = 40;
	private static final Integer MAX_FREEZE_TIME = 2;
	
	private ProgressListener progressListener;
	
	public SwapService(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	/**
	 * 
	 * @param frames - key: frameNum; value: frame
	 * @return
	 */
	public BufferedImage[] processImages(ConcurrentMap<Integer, BufferedImage> frames) {
		long start = System.currentTimeMillis();
		try {
			Runtime.getRuntime().exec("python get_landmarks.py " + STATIC_IMAGE).waitFor();
		} catch (InterruptedException | IOException e) {
			System.err.println(e.getMessage());
		}
		System.out.println("Start swapping " + frames.size() + " frames");

		ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		List<Future<Map<Integer, BufferedImage>>> processes = new LinkedList<>();
		
		try {
			processes = executor.invokeAll(createSwapWorkers(frames));
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}

		Map<Integer, BufferedImage> processedFrames = new ConcurrentHashMap<>();
		for (Future<Map<Integer, BufferedImage>> process : processes) {
			try {
				processedFrames.putAll(process.get(FRAMES_PER_WORKER * MAX_FREEZE_TIME, TimeUnit.SECONDS));
			} catch (Exception ex) {
				System.err.println("Timeout");
			}
		}
		long time = System.currentTimeMillis() - start;
		System.err.println("Time[ms]: " + time);
		System.err.println("Frames per second: " + (frames.size()*1000.0/time));
		return new TreeMap<>(processedFrames).values().toArray(new BufferedImage[0]);
	}

	private Collection<Callable<Map<Integer, BufferedImage>>> createSwapWorkers(ConcurrentMap<Integer, BufferedImage> frames) {
		int framesPerWorker;
		if (THREAD_POOL_SIZE*FRAMES_PER_WORKER >= frames.size()) {
			framesPerWorker = 1 + frames.size() / THREAD_POOL_SIZE;
		} else {
			framesPerWorker = FRAMES_PER_WORKER;
		}
		
		Collection<Callable<Map<Integer, BufferedImage>>> workers = new LinkedList<>();
		
		for (int from=0; from<frames.size(); from+=framesPerWorker) {
			int to = Math.min(frames.size(), from+framesPerWorker);
			workers.add(new SwapWorker(frames, from, to, progressListener));
		}
		
		return workers;
	}
}
