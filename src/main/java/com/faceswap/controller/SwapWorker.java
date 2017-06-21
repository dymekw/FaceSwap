package com.faceswap.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import com.faceswap.Utils;
import org.bridj.util.Pair;

public class SwapWorker implements Callable<Pair<Integer, BufferedImage>> {
	
	private static final String BEFORE_SWAPPING_SUFFIX = "_saved.jpg";
	private static final String AFTER_SWAPPING_SUFFIX = BEFORE_SWAPPING_SUFFIX + "output.jpg";
	
	private BufferedImage image;
    private Integer id;

    public SwapWorker(BufferedImage image, Integer id) {
		this.image = image;
		this.id = id;
	}

	public Pair<Integer, BufferedImage> call() {
		System.out.println(Thread.currentThread().getName() + " start processing frame :" + id);
		Pair<Integer, BufferedImage> result = new Pair<>();
		
        try {
        	if (executeSwapping() == 0) {
        		result = new Pair<>(id, getResultFile());
        	}
        } catch (Exception e) {
        	System.err.println(e.getMessage());
        }
        
        return result;
	}
	
	private BufferedImage getResultFile() throws IOException, InterruptedException {
		File inputfile = new File(id + AFTER_SWAPPING_SUFFIX);
		
		while(!inputfile.exists()) {
			Thread.sleep(10);
		}
		
        BufferedImage swappedFace = ImageIO.read(inputfile);
        inputfile.delete();
        
        return swappedFace;
	}
	
	private int executeSwapping() throws IOException, InterruptedException {
		File outputfile = new File(id + BEFORE_SWAPPING_SUFFIX);
	    ImageIO.write(image, "jpg", outputfile);
	    
    	int result = Runtime.getRuntime().exec(getPythonCommand(id + BEFORE_SWAPPING_SUFFIX)).waitFor();

		outputfile.delete();
		
		return result;
	}
	
	private String getPythonCommand(String fileName) {
		return "python faceswap.py " + fileName + " " + Utils.STATIC_IMAGE;
	}

}
