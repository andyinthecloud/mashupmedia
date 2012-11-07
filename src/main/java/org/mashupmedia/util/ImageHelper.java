package org.mashupmedia.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageHelper {

	public final static int IMAGE_THUMBNAIL_WIDTH = 150;
	public final static int IMAGE_THUMBNAIL_HEIGHT = 150;

	public enum ImageFormatType {
		PNG("png");

		private String value;

		private ImageFormatType(String value) {
			this.value = value;
		}

		public String getFormat() {
			return value;
		}
	}

	public enum ImageType {
		ORIGINAL, THUMBNAIL;
	}

	private static BufferedImage createThumbnail(Image image) {
		int imageType = BufferedImage.TYPE_INT_ARGB;
		BufferedImage thumbnailImage = new BufferedImage(IMAGE_THUMBNAIL_WIDTH, IMAGE_THUMBNAIL_HEIGHT, imageType);
		Graphics2D graphics2d = thumbnailImage.createGraphics();
		graphics2d.setComposite(AlphaComposite.Src);
		graphics2d.drawImage(image, 0, 0, IMAGE_THUMBNAIL_WIDTH, IMAGE_THUMBNAIL_HEIGHT, null);
		graphics2d.dispose();
		return thumbnailImage;
	}

	public static String generateAndSaveThumbnail(long libraryId, String imageFilePath) throws IOException {
		File imageFile = new File(imageFilePath);
		if (!imageFile.exists()) {
			return null;
		}

		Image image = ImageIO.read(imageFile);
		BufferedImage thumbnailImage = createThumbnail(image);
		File thumbnailFile = FileHelper.createAlbumArtThumbnailFile(libraryId);
		ImageIO.write(thumbnailImage, ImageFormatType.PNG.getFormat(), thumbnailFile);
		return thumbnailFile.getAbsolutePath();
	}

}