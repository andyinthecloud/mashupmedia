package org.mashupmedia.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.mashupmedia.util.FileHelper.FileType;

public class ImageHelper {

	public final static int PHOTO_THUMBNAIL_WIDTH = 500;
	public final static int PHOTO_THUMBNAIL_HEIGHT = 500;

	public final static int MUSIC_ALBUM_ART_THUMBNAIL_WIDTH = 150;
	public final static int MUSIC_ALBUM_ART_THUMBNAIL_HEIGHT = 150;

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

	private static BufferedImage createThumbnail(BufferedImage image, int width, int height) {
		BufferedImage thumbnailImage = Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH,
				width, height, Scalr.OP_ANTIALIAS);
		return thumbnailImage;
	}

	public static String generateAndSaveMusicAlbumArtThumbnail(long libraryId, String imageFilePath) throws IOException {
		File thumbnailFile = FileHelper.createThumbnailFile(libraryId, FileType.ALBUM_ART_THUMBNAIL);
		return generateAndSaveThumbnail(libraryId, imageFilePath, thumbnailFile, MUSIC_ALBUM_ART_THUMBNAIL_WIDTH, MUSIC_ALBUM_ART_THUMBNAIL_HEIGHT);
	}
	
	
	public static String generateAndSavePhotoThumbnail(long libraryId, String imageFilePath) throws IOException {
		File thumbnailFile = FileHelper.createThumbnailFile(libraryId, FileType.PHOTO_THUMBNAIL);
		return generateAndSaveThumbnail(libraryId, imageFilePath, thumbnailFile, PHOTO_THUMBNAIL_WIDTH, PHOTO_THUMBNAIL_HEIGHT);				
	}	
	
	protected static String generateAndSaveThumbnail(long libraryId, String imageFilePath, File thumbnailFile, int width, int height) throws IOException {
		File imageFile = new File(imageFilePath);
		if (!imageFile.exists()) {
			return null;
		}

		FileInputStream imageFileInputStream = new FileInputStream(imageFile);
		BufferedImage image = ImageIO.read(imageFileInputStream);
		
		IOUtils.closeQuietly(imageFileInputStream);
		
		BufferedImage thumbnailImage = createThumbnail(image, width, height);
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
