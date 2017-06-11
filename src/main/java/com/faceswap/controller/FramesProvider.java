package com.faceswap.controller;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.faceswap.Utils;
import com.github.sarxos.webcam.Webcam;

public class FramesProvider {
	private ConcurrentMap<Integer, BufferedImage> frames = new ConcurrentHashMap<Integer, BufferedImage>(){
		private static final long serialVersionUID = 1L;

		public BufferedImage put(Integer frameId, BufferedImage frame) {
			if (Objects.nonNull(frame)) {
				super.put(frameId, frame);
			}
			return frame;
		}
	};
	private Webcam webcam = null;
	private Timer timer = null;
	
	public FramesProvider(Webcam webcam) {
		this.webcam = webcam;
	}

	public void startRecording() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
				  frames.put(frames.size(), webcam.getImage());
			  }
			}, 0, Utils.FRAME_CAPTURE_TIME);
	}
	
	public ConcurrentMap<Integer, BufferedImage> stopRecording() {
		if (Objects.nonNull(timer)) {
			timer.cancel();
		}
		return frames;
	}
}
