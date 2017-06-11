package com.faceswap.controller;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import com.faceswap.Utils;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class MovieMaker {

	public void saveAsMovie(BufferedImage[] frames, String fileName) {
		System.out.println("Started making the movie");

		IMediaWriter writer = ToolFactory.makeWriter(fileName);
		Dimension screenBounds = Toolkit.getDefaultToolkit().getScreenSize();
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, screenBounds.width / 2, screenBounds.height / 2);

		for (int i = 0; i<frames.length; i++) {
			frames[i] = convertToType(frames[i], BufferedImage.TYPE_3BYTE_BGR);
			writer.encodeVideo(0, frames[i], i * Utils.FRAME_CAPTURE_TIME, TimeUnit.MILLISECONDS);
		}
		
		writer.close();

		System.out.println("Movie finished");
	}

	private BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
		BufferedImage image;

		if (sourceImage.getType() == targetType) {
			image = sourceImage;
		} else {
			image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
		}

		return image;

	}
}
