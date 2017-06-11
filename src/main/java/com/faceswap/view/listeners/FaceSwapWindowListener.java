package com.faceswap.view.listeners;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

public class FaceSwapWindowListener implements WindowListener{
	
	private Webcam webcam = null;
	private WebcamPanel panel = null;

	public FaceSwapWindowListener(Webcam webcam, WebcamPanel panel) {
		this.webcam = webcam;
		this.panel = panel;
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		webcam.close();
	}

	@Override
	public void windowIconified(WindowEvent e) {
		panel.pause();
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		panel.resume();
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

}
