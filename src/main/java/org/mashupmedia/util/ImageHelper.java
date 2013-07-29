package org.mashupmedia.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

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

		FileInputStream imageFileInputStream = new FileInputStream(imageFile);
		Image image = ImageIO.read(imageFileInputStream);
		
		IOUtils.closeQuietly(imageFileInputStream);
		
		BufferedImage thumbnailImage = createThumbnail(image);
		File thumbnailFile = FileHelper.createAlbumArtThumbnailFile(libraryId);
		FileOutputStream thumbnailFileOutputStream = new FileOutputStream(thumbnailFile);		
		ImageIO.write(thumbnailImage, ImageFormatType.PNG.getFormat(), thumbnailFileOutputStream);
		IOUtils.closeQuietly(thumbnailFileOutputStream);
		return thumbnailFile.getAbsolutePath();
	}
	
	public static ImageType getImageType(String imageTypeValue) {
		imageTypeValue = StringUtils.trimToEmpty(imageTypeValue).toLowerCase();
		if (StringUtils.isEmpty(imageTypeValue)) {
			return ImageType.ORIGINAL;
		}
		
		ImageType[] imageTypes = ImageType.values();
		for (ImageType imageType : imageTypes) {
			if (imageType.toString().toLowerCase().equals(imageTypeValue)) {
				return imageType;
			}
		}

		return ImageType.ORIGINAL;
	}
}
