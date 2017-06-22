package com.faceswap.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

import javax.imageio.ImageIO;

import com.faceswap.Utils;

public class SwapWorker implements Callable<Map<Integer, BufferedImage>> {
	
	private static final String BEFORE_SWAPPING_SUFFIX = "_saved.jpg";
	private static final String AFTER_SWAPPING_SUFFIX = BEFORE_SWAPPING_SUFFIX + "output.jpg";
    
    private ConcurrentMap<Integer, BufferedImage> images;
    private Integer from;
    private Integer to;

    
    public SwapWorker(ConcurrentMap<Integer, BufferedImage> images, Integer from, Integer to) {
    	this.images = images;
    	this.from = from;
    	this.to = to;
    }
    
    @Override
	public Map<Integer, BufferedImage> call() throws Exception {
    	System.out.println(Thread.currentThread().getName() + " start processing frames: [" + from + ", " + to + ")");
		Map<Integer, BufferedImage> swappedImages = new HashMap<>();
		
		if (swap() == 0) {
			for (int i=from; i<to; i++) {
				File inputfile = new File(i + AFTER_SWAPPING_SUFFIX);
				while(!inputfile.exists()) {
					Thread.sleep(10);
				}
				swappedImages.put(i, ImageIO.read(inputfile));
		        inputfile.delete();
			} 
		}
		
		return swappedImages;
	}
    
    private int swap() throws InterruptedException, IOException {
    	Collection<File> outputFiles = new LinkedList<File>();
    	
		for (int i=from; i<to; i++) {
			File outputfile = new File(i + BEFORE_SWAPPING_SUFFIX);
			ImageIO.write(images.get(i), "jpg", outputfile);
			outputFiles.add(outputfile);
		}
		
		int executeSwapping = Runtime.getRuntime().exec(getPythonBulkCommand(BEFORE_SWAPPING_SUFFIX, from, to)).waitFor();
		for (File file : outputFiles) {
			file.delete();
		}
		
		return executeSwapping;
    }
	
	private String getPythonBulkCommand(String fileName, int from, int to) {
		StringJoiner joiner = new StringJoiner(" ");
		String[] command = {
				"python",
				"faceswap.py",
				fileName,
				Utils.STATIC_IMAGE,
				Integer.toString(from),
				Integer.toString(to)
		};
		for (String cmd : command) {
			joiner.add(cmd);
		}
		return joiner.toString();
	}
}
