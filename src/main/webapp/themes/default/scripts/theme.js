

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



