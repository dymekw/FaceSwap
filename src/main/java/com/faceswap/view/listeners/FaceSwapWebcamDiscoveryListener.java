package com.faceswap.view.listeners;

import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamPicker;

public class FaceSwapWebcamDiscoveryListener implements WebcamDiscoveryListener{
	
	private WebcamPicker picker = null;

	public FaceSwapWebcamDiscoveryListener(WebcamPicker picker) {
		this.picker = picker;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void webcamFound(WebcamDiscoveryEvent event) {
		if (picker != null) {
			picker.addItem(event.getWebcam());
		}
	}

	@Override
	public void webcamGone(WebcamDiscoveryEvent event) {
		if (picker != null) {
			picker.removeItem(event.getWebcam());
		}
	}

}
