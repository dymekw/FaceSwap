package com.faceswap.view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * 
 * @author wojciech_dymek
 * singleton
 */
public class ImagePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_IMAGE = "face.jpg";
	private static final ImagePanel INSTANCE = new ImagePanel();
	
	private BufferedImage image;
	private Image scaledImage;
	private Point origin;
	private String filePath;
	
	public static ImagePanel get() {
		return INSTANCE;
	}
	
	private ImagePanel() {
		addComponentListener(new ResizeListener(this));
		origin = new Point();
		loadImage(DEFAULT_IMAGE);
	}
	
	public void recalculateScaledImage() {
		double scaleFactor = getScaleFactor();
		scaledImage = image.getScaledInstance((int)(image.getWidth() * scaleFactor), 
											  (int)(image.getHeight() * scaleFactor), 
											  Image.SCALE_SMOOTH);
		recalculateOrigin();
	}
	
	public String getPath() {
		return filePath;
	}
	
	public void loadImage(String path) {
		loadImage(new File(path));
	}
	
	public void loadImage(File file) {
		filePath = file.getAbsolutePath();
		try {
			image = ImageIO.read(file);
			recalculateScaledImage();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(scaledImage, origin.x, origin.y, this);           
    }
	
	private void recalculateOrigin() {
		if (scaledImage.getHeight(this) < getHeight()) {
			origin.y = (getHeight() - scaledImage.getHeight(this))/2;
		} else {
			origin.y = 0;
		}
		
		if (scaledImage.getWidth(this) < getWidth()) {
			origin.x = (getWidth() - scaledImage.getWidth(this))/2;
		} else {
			origin.x = 0;
		}
	}
	
	private double getScaleFactor() {
		double x = getWidth() / (double)image.getWidth();
		
		if (image.getHeight() * x > getHeight()) {
			return getHeight() / (double)image.getHeight();
		}
		return x;
	}
	
	private static class ResizeListener extends ComponentAdapter {
		private ImagePanel instance;
		
		public ResizeListener(ImagePanel instance) {
			this.instance = instance;
		}
		
		public void componentResized(ComponentEvent e) {
			instance.recalculateScaledImage();
		}
}
}
