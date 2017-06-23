package com.faceswap.view.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

public class ProgressListener implements PropertyChangeListener {
	
	private JProgressBar progressBar;
	private JDialog dialogInstance;

	public ProgressListener(JDialog parent, JProgressBar progressBar) {
		this.progressBar = progressBar;
		this.dialogInstance = parent;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (Objects.isNull(evt)) {
			synchronized (this) {
				progressBar.setValue(progressBar.getValue()+1);
			}
			if (progressBar.getValue() == progressBar.getMaximum()) {
				dialogInstance.dispose();
			}
		}
	} 

}
