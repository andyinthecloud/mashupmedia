$(document).ready(function() {
	var contextUrl = $("#contextUrl").val();
	mashupMedia.setContextUrl(contextUrl);
	$(".jp-previous").click(function() {
		mashupMedia.playPreviousSong();
	});
	$(".jp-next").click(function() {
		mashupMedia.playNextSong();
	});

	$.address.change(function(event) {
		var address = event.value;
		address = address.replace("/", "");

		if (textStartsWith(address, "address-load-album-")) {
			var albumId = getNumberFromText(address);
			mashupMedia.showAlbum(albumId);
		} else if (textStartsWith(address, "category-menu-home")) {
			loadRandomAlbums();
		} else if (textStartsWith(address, "category-menu-albums")) {
			loadAlbums();
		} else if (textStartsWith(address, "category-menu-artists")) {
			loadArtists();
		} else {
			loadRandomAlbums();
		}
	});

});

var mashupMedia = new function() {
	this.contextUrl = $("#contextUrl").val();
	this.setContextUrl = function(contextUrl) {
		this.contextUrl = contextUrl;
	};
	this.playingClass = "playing";
	this.jPlayerId = "#jquery_jplayer_1";
	this.jPlayerContainerId = "#jp_container_1";

	this.loadPlaylist = function() {
		$.post(mashupMedia.contextUrl
				+ "app/ajax/playlist/current-user-playlist", function(data) {
			$("#top-bar-music-player .songs").html(data);
			mashupMedia.loadSong(false);
		});
	};
	this.loadSong = function(isAutoPlay) {
		var playingRow = getPlayingRow();
		if ($(playingRow).length == 0) {
			return;
		}

		var rowId = $(playingRow).attr("id");
		var mediaId = parseId(rowId, "playlist-media-id");

		$.get(mashupMedia.contextUrl + "app/ajax/music/play/" + mediaId,
				function(data) {
					$("#media-player-script").html(data);
					setupJPlayer(isAutoPlay);
				});

	};

	this.playNextSong = function() {
		playRelativeSong(1);
	};

	this.playPreviousSong = function() {
		playRelativeSong(-1);
	};

	this.showAlbum = function(albumId) {

		$.get(mashupMedia.contextUrl + "app/ajax/music/album/" + albumId,
				function(data) {
					$("div.panel div.content").html(data);
				});

	};
	
	this.playAlbum = function(albumId) {
		$.post(mashupMedia.contextUrl + "/app/ajax/playlist/play-album", {
			albumId : albumId
		}, function(data) {
			$("#top-bar-music-player .songs").html(data);
			mashupMedia.loadSong(true);
//			loadCurrentSong();
		});		
	};
}

function playRelativeSong(offset) {
	var playingRow = getPlayingRow();
	if ($(playingRow).length == 0) {
		return;
	}

	var playingIndex = $("#top-bar-music-player .songs table tbody tr").index(
			playingRow);

	var nextPlayingRow = $("#top-bar-music-player .songs table tbody tr")[playingIndex
			+ offset];
	if ($(nextPlayingRow).length == 0) {
		return;
	}

	$(playingRow).removeClass(mashupMedia.playingClass);
	$(nextPlayingRow).addClass(mashupMedia.playingClass);
	mashupMedia.loadSong(true);

}

function getPlayingRow() {
	var playingRow = $("#top-bar-music-player .songs table tbody tr."
			+ mashupMedia.playingClass);
	if ($(playingRow).length > 0) {
		return playingRow;
	}

	playingRow = $("#top-bar-music-player .songs table tbody tr").index(1);
	return playingRow;
}

function parseId(text, identifier) {
	if (!endsWith(identifier, "-")) {
		identifier = identifier + "-";
	}

	var regExp = new RegExp(identifier + "\\d+");
	var matchValue = text.match(regExp);
	var id = String(matchValue).replace(identifier, "");
	return id;
}

function endsWith(text, suffix) {
	return text.indexOf(suffix, text.length - suffix.length) !== -1;
}

function getNumberFromText(text) {
	var number = text.match(/\d+/g);
	return number;
}

function textStartsWith(text, startsWithValue) {
	if (text.indexOf(startsWithValue) > -1) {
		return true;
	}
	return false;
}


function loadRandomAlbums() {
	$.get(mashupMedia.contextUrl + "app/ajax/music/random-albums",
		function(data) {
			$("div.panel div.content").html(data);
	});
}

/*
function loadAlbums() {
	$.get("<c:url value="/app/ajax/music/albums" />", function(data) {
		$("div.panel div.content").html(data);
	});
}

function loadArtists() {
	$.get("<c:url value="/app/ajax/music/artists" />", function(data) {
		$("div.panel div.content").html(data);
	});
}

function playAlbum(albumId) {
	$.post("<c:url value="/app/ajax/playlist/play-album" />", {
		albumId : albumId
	}, function(data) {
		$("#top-bar-music-player .songs").html(data);
		loadCurrentSong();
	});
}
*/
