package com.faceswap.view;

import java.awt.BorderLayout;
import java.awt.event.ItemListener;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.faceswap.view.listeners.FaceSwapItemListener;
import com.faceswap.view.listeners.FaceSwapWebcamDiscoveryListener;
import com.faceswap.view.listeners.FaceSwapWebcamListener;
import com.faceswap.view.listeners.FaceSwapWindowListener;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;

public class FaceSwapAppFrame extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	
	protected Webcam webcam = null;
	protected WebcamPanel panel = null;
	protected WebcamPicker picker = null;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new FaceSwapAppFrame());
	}

	@Override
	public void run() {
		
		setTitle("Java Webcam Capture POC");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		WebcamListener webcamListener = new FaceSwapWebcamListener();
		
		picker = new WebcamPicker();
		
		webcam = picker.getSelectedWebcam();
		if (webcam == null) {
			System.out.println("No webcams found...");
			System.exit(1);
		}
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.addWebcamListener(webcamListener);
		
		panel = new WebcamPanel(webcam, false);
		panel.setFPSDisplayed(true);
		
		ItemListener itemListener = new FaceSwapItemListener(webcam, panel, this, webcamListener);
		picker.addItemListener(itemListener);
		
		WebcamDiscoveryListener webcamDiscoveryListener = new FaceSwapWebcamDiscoveryListener(picker);
		Webcam.addDiscoveryListener(webcamDiscoveryListener);
		
		WindowListener windowListener = new FaceSwapWindowListener(webcam, panel);
		addWindowListener(windowListener);
		
		add(picker, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
		
		Thread t = new Thread() {

			@Override
			public void run() {
				panel.start();
			}
		};
		t.setName("example-starter");
		t.setDaemon(true);
		t.start();
	}

}
