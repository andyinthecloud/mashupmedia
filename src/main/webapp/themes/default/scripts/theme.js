$(window)
		.load(
				function() {
					/*
					var theWindow = $(window), $backgroundImage = $("#background-image"), aspectRatio = $backgroundImage
							.width()
							/ $backgroundImage.height();

					function resizeBackgroundImage() {

						if ((theWindow.width() / theWindow.height()) < aspectRatio) {
							$backgroundImage.removeClass().addClass(
									'full-height');
						} else {
							$backgroundImage.removeClass().addClass(
									'full-width');
						}

					}

					theWindow.resize(function() {
						resizeBackgroundImage();
					}).trigger("resize");
					*/
				});

$(document).ready(function() {
	$(".button").hover(function() {
		$(this).addClass("hover");
	}, function() {
		$(this).removeClass("hover");
	});

});

/*
function processBackground(themePath, pageType) {

	var backgroundImagePath = getBackgroundImage(pageType);
	$("#background-image").attr("src", themePath + backgroundImagePath);
	$("#background-image").load();
	$("#background-image").show();
}

function getBackgroundImage(type) {

	var images = new Array();

	if (type == "default") {
		images = getDefaultBackgroundImages();
	} else if (type == "music") {
		images = getMusicBackgroundImages();
	} else {
		images = getDefaultBackgroundImages();
	}

	var randomImageIndex = Math.floor((Math.random() * images.length));
	return images[randomImageIndex];

}

function getDefaultBackgroundImages() {
	var backgroundImages = new Array();
	backgroundImages[0] = "/images/default/background-01.jpg";
	backgroundImages[1] = "/images/default/background-02.jpg";
	return backgroundImages;
}

function getMusicBackgroundImages() {
	var backgroundImages = new Array();
	backgroundImages[0] = "/images/music/background-01.jpg";
	backgroundImages[1] = "/images/music/background-02.jpg";
	backgroundImages[2] = "/images/music/background-03.jpg";
	backgroundImages[3] = "/images/music/background-04.jpg";
	return backgroundImages;
}
*/
