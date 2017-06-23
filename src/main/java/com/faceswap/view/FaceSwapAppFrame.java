package com.faceswap.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.apache.log4j.BasicConfigurator;

import com.faceswap.controller.FramesProvider;
import com.faceswap.view.fileFilter.ImageFilter;
import com.faceswap.view.listeners.FaceSwapItemListener;
import com.faceswap.view.listeners.FaceSwapWebcamDiscoveryListener;
import com.faceswap.view.listeners.FaceSwapWebcamListener;
import com.faceswap.view.listeners.FaceSwapWindowListener;
import com.faceswap.view.listeners.RecordingButtonListener;
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
	
	protected JToggleButton recordingButton = createStartRecordingButton();
	protected FramesProvider framesProvider = null;
	
	protected JFileChooser fileChooser = new JFileChooser();
	protected ImagePanel imagePanel = ImagePanel.get();
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		SwingUtilities.invokeLater(new FaceSwapAppFrame());
	}

	@Override
	public void run() {
		
		setTitle("FaceSwap hołm ediszyn");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new ImageFilter());
		
		WebcamListener webcamListener = new FaceSwapWebcamListener(recordingButton);
		
		picker = new WebcamPicker();
		
		webcam = picker.getSelectedWebcam();
		if (webcam == null) {
			System.out.println("No webcams found...");
			System.exit(1);
		}
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.addWebcamListener(webcamListener);
		
		framesProvider = new FramesProvider(webcam);
		recordingButton.addActionListener(createRecordingButtonListener(framesProvider));
		
		panel = new WebcamPanel(webcam, false);
		panel.setFPSDisplayed(true);
		
		ItemListener itemListener = new FaceSwapItemListener(webcam, panel, this, webcamListener);
		picker.addItemListener(itemListener);
		
		WebcamDiscoveryListener webcamDiscoveryListener = new FaceSwapWebcamDiscoveryListener(picker);
		Webcam.addDiscoveryListener(webcamDiscoveryListener);
		
		WindowListener windowListener = new FaceSwapWindowListener(webcam, panel);
		addWindowListener(windowListener);
		
		add(picker, BorderLayout.NORTH);
		
		JPanel southPanel = new JPanel(new GridLayout(1, 2));
		southPanel.add(recordingButton);
		southPanel.add(createChooseFileButton());
		add(southPanel, BorderLayout.SOUTH);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(1, 2));
		centerPanel.add(panel);
		centerPanel.add(imagePanel);
		
		add(centerPanel, BorderLayout.CENTER);
		
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
	
	private JToggleButton createStartRecordingButton() {
		JToggleButton startRecording = new JToggleButton("Start recording");
		startRecording.setEnabled(false);

		return startRecording;
	}
	
	private JButton createChooseFileButton() {
		JButton result = new JButton("Choose face to swap");
		
		result.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(FaceSwapAppFrame.this);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					imagePanel.loadImage(fileChooser.getSelectedFile());
		        }
			}
		});
		
		return result;
	}
	
	private ActionListener createRecordingButtonListener(FramesProvider framesProvider) {
		return new RecordingButtonListener(recordingButton, framesProvider, this);
	}

}
