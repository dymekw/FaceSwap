package com.faceswap.controller;

import java.awt.image.BufferedImage;
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
import java.util.stream.Collectors;

import org.bridj.util.Pair;

public class SwapService {

	private static final Integer THREAD_POOL_SIZE = 10;

	/**
	 * 
	 * @param frames - key: frameNum; value: frame
	 * @return
	 */
	public BufferedImage[] processImages(ConcurrentMap<Integer, BufferedImage> frames) {
		System.out.println("Start swapping " + frames.size() + " frames");

		ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		List<Future<Pair<Integer, BufferedImage>>> processes = new LinkedList<>();
		
		try {
			processes = executor.invokeAll(createSwapWorkers(frames));
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}

		Map<Integer, BufferedImage> processedFrames = new ConcurrentHashMap<>();
		for (Future<Pair<Integer, BufferedImage>> process : processes) {
			try {
				Pair<Integer, BufferedImage> processedFrame = process.get(20, TimeUnit.SECONDS);
				processedFrames.put(processedFrame.getFirst(), processedFrame.getSecond());
			} catch (Exception ex) {
				System.err.println("Timeout");
			}
		}

		return new TreeMap<>(processedFrames).values().toArray(new BufferedImage[0]);
	}

	private Collection<Callable<Pair<Integer, BufferedImage>>> createSwapWorkers(ConcurrentMap<Integer, BufferedImage> frames) {
		return frames.keySet()
				.stream()
				.map(frameId -> new SwapWorker(frames.get(frameId), frameId))
				.collect(Collectors.toList());
	}
}
