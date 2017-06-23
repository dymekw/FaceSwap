package com.faceswap.view.listeners;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentMap;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.SwingWorker;

import com.faceswap.controller.FramesProvider;
import com.faceswap.controller.MovieMaker;
import com.faceswap.controller.SwapService;

public class RecordingButtonListener implements ActionListener {
	
	private JToggleButton buttonInstance;
	private FramesProvider framesProvider;
	private JFrame parentFrameInstance;
	private SwapService service;
	
	private static final String FILE_NAME = "myMovie.mp4";
	
	public RecordingButtonListener(JToggleButton buttonInstance, FramesProvider framesProvider, JFrame parentFrame) {
		this.buttonInstance = buttonInstance;
		this.framesProvider = framesProvider;
		this.parentFrameInstance = parentFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (buttonInstance.isSelected()) {
			buttonInstance.setText("Stop recording");
			framesProvider.startRecording();
		} else {
			buttonInstance.setEnabled(false);
			buttonInstance.setText("Start recording");
			displayProgressDialog(service);
		}
	}
	
	private void displayProgressDialog(SwapService service) {		
		JDialog progressDialog = new JDialog(parentFrameInstance, "", Dialog.ModalityType.DOCUMENT_MODAL);

		progressDialog.setUndecorated(true);
		progressDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		Dimension dialogSize = new Dimension(2*parentFrameInstance.getWidth()/3, parentFrameInstance.getHeight()/3);
		progressDialog.setMinimumSize(dialogSize);
		progressDialog.setSize(dialogSize);
		progressDialog.setResizable(false);
		progressDialog.setLocationRelativeTo(parentFrameInstance);
		
		progressDialog.add(getProgressBar(progressDialog));

		progressDialog.pack();
		progressDialog.setVisible(true);
	}
	
	private JProgressBar getProgressBar(JDialog parent) {
		JProgressBar progressBar = new JProgressBar(0, framesProvider.countRecordedFrames());
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		ProgressListener progressListener = new ProgressListener(parent, progressBar);
		service = new SwapService(progressListener);
		new BackgroundWorker(framesProvider, service, buttonInstance).execute();
		progressBar.addPropertyChangeListener(progressListener);
		
		Dimension size = new Dimension((int)(parent.getWidth()*0.9d), (int)(parent.getHeight()*0.25d));
		progressBar.setSize(size);
		progressBar.setPreferredSize(size);
		return progressBar;
	}
	
	private static class BackgroundWorker extends SwingWorker<Object, Object> {

		private FramesProvider framesProvider;
		private AbstractButton buttonInstance;
		private SwapService swapService;
		private MovieMaker movieMaker;

		public BackgroundWorker(FramesProvider framesProvider, SwapService swapService, AbstractButton buttonInstance) {
			this.framesProvider = framesProvider;
			this.buttonInstance = buttonInstance;
			this.swapService = swapService;
			
			movieMaker = new MovieMaker();
		}

		@Override
		protected Object doInBackground() throws Exception {
			createMovie(framesProvider.stopRecording());
			return null;
		}

		@Override
		protected void done() {
			buttonInstance.setEnabled(true);
		}

		private void createMovie(ConcurrentMap<Integer, BufferedImage> frames) {
			System.out.println("RecordingButtonListener.BackgroundWorker.createMovie()");
			movieMaker.saveAsMovie(swapService.processImages(frames), FILE_NAME);
		}
	}

}
