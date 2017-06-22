package com.faceswap.view.listeners;

import javax.swing.AbstractButton;

import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;

public class FaceSwapWebcamListener implements WebcamListener{
	
	private AbstractButton recordingButton;
	
	public FaceSwapWebcamListener(AbstractButton recordingButton) {
		this.recordingButton = recordingButton;
	}

	@Override
	public void webcamClosed(WebcamEvent arg0) {
	}

	@Override
	public void webcamDisposed(WebcamEvent arg0) {
	}

	@Override
	public void webcamImageObtained(WebcamEvent arg0) {
	}

	@Override
	public void webcamOpen(WebcamEvent arg0) {
		recordingButton.setEnabled(true);
	}

}
