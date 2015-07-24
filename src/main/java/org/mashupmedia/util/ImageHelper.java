package org.mashupmedia.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;
import org.mashupmedia.util.FileHelper.FileType;

public class ImageHelper {
	private static Logger LOGGER = Logger.getLogger(ImageHelper.class);

	public final static int PHOTO_THUMBNAIL_WIDTH = 200;
	public final static int PHOTO_THUMBNAIL_HEIGHT = 200;
	
	public final static int PHOTO_WEB_OPTIMISED_WIDTH = 600;
	public final static int PHOTO_WEB_OPTIMISED_HEIGHT = 600;
	

	public final static int MUSIC_ALBUM_ART_THUMBNAIL_WIDTH = 200;
	public final static int MUSIC_ALBUM_ART_THUMBNAIL_HEIGHT = 200;

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
		ORIGINAL, THUMBNAIL, WEB_OPTIMISED;
	}

	private static BufferedImage createThumbnail(BufferedImage image,
			int width, int height) {
		BufferedImage thumbnailImage = null;
		try {
			thumbnailImage = Scalr.resize(image, Scalr.Method.SPEED,
					Scalr.Mode.FIT_TO_WIDTH, width, height, Scalr.OP_ANTIALIAS);			
		} catch (Exception e) {
			LOGGER.error("Error resizing image.", e);
		}
		return thumbnailImage;
	}

//	private static BufferedImage resizeImageDefault(BufferedImage image,
//			int width, int height) {
//		int imageType = image.getType();
//		if (imageType == 0) {
//			imageType = BufferedImage.TYPE_INT_ARGB;
//		}
//
//		BufferedImage resizedImage = new BufferedImage(width, height, imageType);
//		Graphics2D graphics2d = resizedImage.createGraphics();
//		graphics2d.drawImage(image, 0, 0, width, height, null);
//		graphics2d.dispose();
//		graphics2d.setComposite(AlphaComposite.Src);
//		graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//		graphics2d.setRenderingHint(RenderingHints.KEY_RENDERING,
//				RenderingHints.VALUE_RENDER_QUALITY);
//		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//				RenderingHints.VALUE_ANTIALIAS_ON);
//		return resizedImage;
//	}

	public static String generateAndSaveMusicAlbumArtThumbnail(long libraryId,
			String imageFilePath) throws IOException {
		File thumbnailFile = FileHelper.createMediaItemFile(libraryId,
				FileType.ALBUM_ART_THUMBNAIL);
		return generateAndSaveThumbnail(libraryId, imageFilePath,
				thumbnailFile, MUSIC_ALBUM_ART_THUMBNAIL_WIDTH,
				MUSIC_ALBUM_ART_THUMBNAIL_HEIGHT);
	}

	public static String generateAndSavePhoto(long libraryId,
			String imageFilePath, ImageType imageType) throws IOException {
		
		if (StringUtils.isBlank(imageFilePath)) {
			return null;
		}
		
		FileType fileType = FileType.PHOTO_THUMBNAIL;
		int width = PHOTO_THUMBNAIL_WIDTH;
		int height = PHOTO_THUMBNAIL_HEIGHT;
		
		
		if (imageType == ImageType.WEB_OPTIMISED) {
			fileType = FileType.PHOTO_WEB_OPTIMISED;
			width = PHOTO_WEB_OPTIMISED_WIDTH;
			height = PHOTO_WEB_OPTIMISED_HEIGHT;
		}
		
		File thumbnailFile = FileHelper.createMediaItemFile(libraryId,
				fileType);
		return generateAndSaveThumbnail(libraryId, imageFilePath,
				thumbnailFile, width, height);
	}
	
	protected static String generateAndSaveThumbnail(long libraryId,
			String imageFilePath, File thumbnailFile, int width, int height)
			throws IOException {
		File imageFile = new File(imageFilePath);
		if (!imageFile.exists()) {
			return null;
		}

		FileInputStream imageFileInputStream = new FileInputStream(imageFile);
		BufferedImage image = ImageIO.read(imageFileInputStream);
		imageFileInputStream.close();
		IOUtils.closeQuietly(imageFileInputStream);

		BufferedImage thumbnailImage = createThumbnail(image, width, height);
		FileOutputStream thumbnailFileOutputStream = new FileOutputStream(
				thumbnailFile);
		ImageIO.write(thumbnailImage, ImageFormatType.PNG.getFormat(),
				thumbnailFileOutputStream);
		thumbnailFileOutputStream.close();		
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
