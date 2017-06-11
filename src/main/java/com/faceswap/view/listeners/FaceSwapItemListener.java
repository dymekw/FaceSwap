package com.faceswap.view.listeners;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class FaceSwapItemListener implements ItemListener{
	
	private Webcam webcam = null;
	private WebcamPanel panel = null;
	private JFrame frame = null;
	private WebcamListener webcamListener = null;

	public FaceSwapItemListener(Webcam webcam, WebcamPanel panel, JFrame frame, WebcamListener webcamListener) {
		this.webcam = webcam;
		this.panel = panel;
		this.frame = frame;
		this.webcamListener = webcamListener;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem() != webcam) {
			if (webcam != null) {

				panel.stop();

				frame.remove(panel);

				webcam.removeWebcamListener(webcamListener);
				webcam.close();

				webcam = (Webcam) e.getItem();
				webcam.setViewSize(WebcamResolution.VGA.getSize());
				webcam.addWebcamListener(webcamListener);

				panel = new WebcamPanel(webcam, false);
				panel.setFPSDisplayed(true);

				frame.add(panel, BorderLayout.CENTER);
				frame.pack();

				Thread t = new Thread() {

					@Override
					public void run() {
						panel.start();
					}
				};
				t.setName("example-stoper");
				t.setDaemon(true);
				t.start();
			}
		}
	}

}
