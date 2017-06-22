package com.faceswap.test;

import com.faceswap.controller.FramesProvider;
import com.faceswap.controller.MovieMaker;
import com.faceswap.controller.SwapService;
import com.faceswap.view.listeners.FaceSwapWebcamListener;
import com.github.sarxos.webcam.WebcamEvent;

public class TestWebcamListener extends FaceSwapWebcamListener{
	
	private FramesProvider framesProvider = null;
	private SwapService swapService = null;
	private MovieMaker movieMaker = null;

	public TestWebcamListener(FramesProvider framesProvider, SwapService swapService, MovieMaker movieMaker) {
		super(null);
		this.framesProvider = framesProvider;
		this.swapService = swapService;
		this.movieMaker = movieMaker;
	}

	@Override
	public void webcamOpen(WebcamEvent arg0) {
		super.webcamOpen(arg0);
		
		framesProvider.startRecording();
	}
	
	@Override
	public void webcamClosed(WebcamEvent arg0) {
		movieMaker.saveAsMovie(swapService.processImages(framesProvider.stopRecording()), "testMovie.mp4");
	}
	
}
