package org.mashupmedia.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Rotation;
import org.mashupmedia.util.FileHelper.FileType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageHelper {

	public final static int THUMBNAIL_WIDTH = 500;
	public final static int THUMBNAIL_HEIGHT = 500;


	public final static int PHOTO_THUMBNAIL_WIDTH = 500;
	public final static int PHOTO_THUMBNAIL_HEIGHT = 500;

	public final static int PHOTO_WEB_OPTIMISED_WIDTH = 1000;
	public final static int PHOTO_WEB_OPTIMISED_HEIGHT = 1000;

	public final static int MUSIC_ALBUM_ART_THUMBNAIL_WIDTH = 500;
	public final static int MUSIC_ALBUM_ART_THUMBNAIL_HEIGHT = 500;




	public enum ImageFormatType {
		PNG("png"), JPEG("JPEG");

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

	public enum ImageRotationType {
		FLIP_HORIZONTAL(2, Rotation.FLIP_HORZ), CLOCKWISE_180(3, Rotation.CW_180), FLIP_VERTICAL(4, Rotation.FLIP_VERT), CLOCKWISE_90(6,
				Rotation.CW_90), CLOCKWISE_270(8, Rotation.CW_270);

		private int exifTagOrientation;
		private Rotation rotation;

		private ImageRotationType(int exifTagOrientation, Rotation rotation) {
			this.exifTagOrientation = exifTagOrientation;
			this.rotation = rotation;
		}

	}

	public static ImageRotationType getImageRotationType(int exifTagOrientation) {
		ImageRotationType[] imageRotationTypes = ImageRotationType.values();
		for (ImageRotationType imageRotationType : imageRotationTypes) {
			if (exifTagOrientation == imageRotationType.exifTagOrientation) {
				return imageRotationType;
			}
		}
		return null;
	}

	private static BufferedImage processImage(BufferedImage image, int width, int height, ImageRotationType imageRotationType) {
		BufferedImage processedImage = null;
		try {

			processedImage = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, width, height);

			if (imageRotationType != null) {
				processedImage = Scalr.rotate(processedImage, imageRotationType.rotation);
			}

		} catch (Exception e) {
			log.error("Error resizing image.", e);
		}
		return processedImage;
	}

	public static String generateAndSaveMusicAlbumArtThumbnail(String userFolderName, long libraryId, String imageFilePath) throws IOException {
		File thumbnailFile = FileHelper.createMediaItemFile(userFolderName, libraryId, FileType.ALBUM_ART_THUMBNAIL);
		return generateAndSaveImage(libraryId, imageFilePath, thumbnailFile, MUSIC_ALBUM_ART_THUMBNAIL_WIDTH, MUSIC_ALBUM_ART_THUMBNAIL_HEIGHT, null);
	}

	public static String generateAndSaveImage(String userFolderName, long libraryId, String imageFilePath, ImageType imageType, ImageRotationType imageRotationType)
			throws IOException {

		if (StringUtils.isBlank(imageFilePath)) {
			throw new IOException("Unable to read image file path is empty");
		}

		FileType fileType = FileType.PHOTO_THUMBNAIL;
		int width = PHOTO_THUMBNAIL_WIDTH;
		int height = PHOTO_THUMBNAIL_HEIGHT;

		if (imageType == ImageType.WEB_OPTIMISED) {
			fileType = FileType.PHOTO_WEB_OPTIMISED;
			width = PHOTO_WEB_OPTIMISED_WIDTH;
			height = PHOTO_WEB_OPTIMISED_HEIGHT;
		}

		File file = FileHelper.createMediaItemFile(userFolderName, libraryId, fileType);
		return generateAndSaveImage(libraryId, imageFilePath, file, width, height, imageRotationType);
	}

	protected static String generateAndSaveImage(long libraryId, String imageFilePath, File file, int width, int height,
			ImageRotationType imageRotationType) throws IOException {
		File imageFile = new File(imageFilePath);
		if (!imageFile.exists()) {
			throw new IOException("Unable to read image file path is empty");
		}

		FileInputStream imageFileInputStream = new FileInputStream(imageFile);
		BufferedImage image = ImageIO.read(imageFileInputStream);
		imageFileInputStream.close();
		IOUtils.closeQuietly(imageFileInputStream);

		BufferedImage bufferedImage = processImage(image, width, height, imageRotationType);
		if (bufferedImage == null) {
			throw new IOException("Unable to read image");
		}
		
		FileOutputStream outputStream = new FileOutputStream(file);
		ImageIO.write(bufferedImage, ImageFormatType.JPEG.getFormat(), outputStream);
		outputStream.close();
		IOUtils.closeQuietly(outputStream);

		return file.getAbsolutePath();
	}

	public static void generateThumbnail(InputStream inputStream, Path path) throws IOException{
		BufferedImage image = ImageIO.read(inputStream);
		IOUtils.closeQuietly(inputStream);

		BufferedImage thumbnailImage = processImage(image, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, null);
		if (thumbnailImage == null) {
			throw new IOException("Unable to read image");
		}

		ImageIO.write(thumbnailImage, ImageFormatType.PNG.getFormat(), path.toFile()); 
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
