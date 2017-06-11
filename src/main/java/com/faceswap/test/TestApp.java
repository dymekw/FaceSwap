package com.faceswap.test;

import javax.swing.SwingUtilities;

import com.faceswap.controller.FramesProvider;
import com.faceswap.controller.MovieMaker;
import com.faceswap.controller.SwapService;
import com.faceswap.view.FaceSwapAppFrame;
import com.github.sarxos.webcam.WebcamListener;

public class TestApp extends FaceSwapAppFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new TestApp());
	}
	
	@Override
	public void run() {
		super.run();
		WebcamListener webcamListener = new TestWebcamListener(new FramesProvider(webcam), new SwapService(), new MovieMaker());
		
		webcam.addWebcamListener(webcamListener);
	}
	
}
