<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

var isLoadingContent = false;
var serialisedSearchForm = "";

var addressRandomAlbums = "address-random-albums";
var addressQuickSearchMediaItems = "address-quick-search-media-items";
var addressListArtists = "address-list-artists";
var addressListAlbums = "address-list-albums";
var addressFilterAlbumsByLetter = "address-filter-albums-letter-";
var addressAlbum = "address-load-album";
var addressArtist = "address-artist-";


$(document).ready(function() {
	
	$("div.panel").ajaxComplete(function(e, xhr, settings) {
		var responseHtml = xhr.responseText;
		if (responseHtml.indexOf("@LOGGED-OUT@") >= 0) {
			window.location.reload();
		}		
	});	
	
		
	var contextUrl = "<c:url value="/" />";
	mashupMedia.setContextUrl(contextUrl);
	$(".jp-previous").click(function() {
		mashupMedia.playPreviousSong();
	});
	$(".jp-next").click(function() {
		mashupMedia.playNextSong();
	});

	$.address.init(function(event) {
		$("#quick-search").address();
	});
	
	$.address.change(function(event) {
		isLoadingContent = false;
		
		if($("#top-bar-music-player .songs").length > 0) {			
			closeSongPlaylist();	
		}
		
		var isScrollToTop = false;
		var address = event.value;
		address = address.replace("/", "");
		address = $.trim(address);
		
		if (textStartsWith(address, addressAlbum)) {
			var albumId = getNumberFromText(address);
			mashupMedia.showAlbum(albumId);
			isScrollToTop = true;
		} else if (textStartsWith(address, addressListArtists)) {
			loadArtists();
			isScrollToTop = true;
		} else if (textStartsWith(address, addressListAlbums)) {
			mashupMedia.filterAlbumsSearchLetter = "";
			mashupMedia.filterPageNumber = 0;
			loadAlbums(false);
			isScrollToTop = true;
		} else if (textStartsWith(address, addressFilterAlbumsByLetter)) {
			var searchLetter = address.replace(addressFilterAlbumsByLetter, "");
			mashupMedia.filterAlbumsSearchLetter = searchLetter;
			mashupMedia.filterPageNumber = 0;
			loadAlbums(false);			
			isScrollToTop = true;
		} else if (textStartsWith(address, addressRandomAlbums) || address == "") {
			loadRandomAlbums(false);
			isScrollToTop = true;
		} else if (textStartsWith(address, addressArtist)) {
			var artistId = parseId(address, addressArtist);
			if (isNaN(artistId)) {
				return;
			}
			loadArtist(artistId)
			isScrollToTop = true;
		} else if (textStartsWith(address, addressQuickSearchMediaItems)) {
			serialisedSearchForm = $("#quick-search").serialize();
			loadSongSearchResults(false);			
			isScrollToTop = true;
		} 
		
		if (isScrollToTop) {
			window.scrollTo(0, 0);
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
	this.filterPageNumber = 0;
	this.filterAlbumsSearchLetter = "";

	this.loadPlaylist = function() {
		$.post(mashupMedia.contextUrl
				+ "app/ajax/playlist/current-user-playlist", function(data) {
			$("#top-bar-music-player .songs").html(data);
			mashupMedia.loadSong(false);
		});
	};
	this.loadSong = function(isAutoPlay) {
		var playingRow = getPlayingRow();
		
		if (playingRow <= 0) {
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
		$.get(mashupMedia.contextUrl + "app/ajax/music/album/" + albumId, function(data) {
			$("div.panel div.content").html(data);
		});
	};
	
	this.playArtist = function(artistId) {
		$.post(mashupMedia.contextUrl + "app/ajax/playlist/play-artist", {
			"artistId" : artistId
		}, function(data) {
			$("#top-bar-music-player .songs").html(data);
			mashupMedia.loadSong(true);
		});		
	};

	this.playAlbum = function(albumId) {
		$.post(mashupMedia.contextUrl + "app/ajax/playlist/play-album", {
			"albumId" : albumId
		}, function(data) {
			$("#top-bar-music-player .songs").html(data);
			mashupMedia.loadSong(true);
		});		
	};
	
	this.playSong = function(songId) {
		$.post(mashupMedia.contextUrl + "app/ajax/playlist/play-song", {
			"songId" : songId
		}, function(data) {
			$("#top-bar-music-player .songs").html(data);
			mashupMedia.loadSong(true);
		});
	};

	this.appendArtist = function(artistId) {
		$.post(mashupMedia.contextUrl + "app/ajax/playlist/append-artist", {
			"artistId" : artistId
		}, function(data) {
			$("#top-bar-music-player .songs table.song-playlist tbody").append(data);
			loadNextSongIfNotPlaying();
		});
	};

	this.appendAlbum = function(albumId) {
		$.post(mashupMedia.contextUrl + "app/ajax/playlist/append-album", {
			"albumId" : albumId
		}, function(data) {
			$("#top-bar-music-player .songs table.song-playlist tbody").append(data);
			loadNextSongIfNotPlaying();
		});		
	};
	
	this.appendSong = function(songId) {
		$.post(mashupMedia.contextUrl + "app/ajax/playlist/append-song", {
			"songId" : songId
		}, function(data) {
			$("#top-bar-music-player .songs table.song-playlist tbody").append(data);
			loadNextSongIfNotPlaying();
		});		
	};

	this.destroyPlayer = function() {
		$(mashupMedia.jPlayerId).jPlayer("destroy");
	};
	
	this.saveCurrentPlaylist = function() {
		var playlistId = $("#current-playlist-id").val();
		var mediaItemIds = new Array();
		$("#top-bar-music-player table.song-playlist tbody tr").each(function(index) {
			var rowId = $(this).attr("id");
			var mediaItemId = parseId(rowId, "playlist-media-id");
			mediaItemIds[index] = mediaItemId;
		});
		
		$.post(mashupMedia.contextUrl + "app/ajax/playlist/save", {
			"playlistId" : playlistId,
			"mediaItemIds" : mediaItemIds
		}, function(data) {
		});	
		
	};
	
	this.clearPlaylist = function() {
		$("#top-bar-music-player table.song-playlist tbody tr").remove();
		mashupMedia.destroyPlayer();	
		mashupMedia.showEmptySongInfo();
		mashupMedia.loadSong(false);
	};
	
	this.showEmptySongInfo = function() {		
		mashupMedia.showSongInfo("", "", false, 0, 0);
	};
	
	this.showSongInfo = function(songTitle, artistName, isShowVoteButtons, albumId, mediaItemId) {		
		if (songTitle == "") {
			songTitle = "<spring:message code="music.playlist.current-song.empty" />";
		}
		
		var albumArtImageSrc = "<c:url value="/images/no-album-art.png" />";
		if (albumId > 0) {
			albumArtImageSrc = mashupMedia.contextUrl + "app/music/album-art-thumbnail/" + albumId;
		}
		
		
		$("#current-song td.song-title .title").text(songTitle);	
		$("#current-song td.song-title .artist-name").text(artistName);
		if (isShowVoteButtons) {
			$("#current-song .vote").show();	
		} else {
			$("#current-song .vote").hide();
		}
				
		$("#current-song .album-art img").attr("src", albumArtImageSrc);
		$("#current-song-id").val(mediaItemId);
	};
	
	
}

function loadNextSongIfNotPlaying() {
	var currentSongId = $("#current-song-id").val();
	if (currentSongId < 1) {
		mashupMedia.loadSong(true);
	}
}

function loadLink(path) {
	var windowLocation = window.location;
	windowLocation = windowLocation.replace()
	
	window.location = window.location + "#" + path;
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

function loadRandomAlbums(isAppend) {
	if (isLoadingContent) {
		return;
	}
	
	isLoadingContent = true;
	$.get(mashupMedia.contextUrl + "app/ajax/music/random-albums",
		function(data) {
			if (isAppend) {
				$("div.panel div.content").append(data);	
			} else {
				$("div.panel div.content").html(data);
			}
			pauseScrollLoadMore();						
	});
}


function loadSongSearchResults(isAppend) {
	if (isLoadingContent) {
		return;
	}
	
	isLoadingContent = true;
	
	$.post(mashupMedia.contextUrl + "app/ajax/search/media-items?" + serialisedSearchForm, {
		"pageNumber" : mashupMedia.filterPageNumber,
	},	function(data) {
		if (isAppend) {
			$("div.panel div.content").append(data);	
		} else {
			$("div.panel div.content").html(data);
		}				
		pauseScrollLoadMore();		
	});				

}

function loadAlbums(isAppend) {	
	if (isLoadingContent) {
		return;
	}
	isLoadingContent = true;
	
	$.get("<c:url value="/app/ajax/music/albums" />", {
		"pageNumber" : mashupMedia.filterPageNumber,
		"searchLetter": mashupMedia.filterAlbumsSearchLetter
	}, function(data) {
		if (isAppend) {
			$("div.panel div.content").append(data);	
		} else {
			$("div.panel div.content").html(data);
		}				
		pauseScrollLoadMore();
	});
}


function loadArtists() {
	$.get("<c:url value="/app/ajax/music/artists" />", function(data) {
		$("div.panel div.content").html(data);
	});
}


function loadArtist(artistId) {
	$.get("<c:url value="/app/ajax/music/artist/" />" + artistId, function(data) {
		$("div.panel div.content").html(data);
	});
}

function prepareShowPageTitle() {
	var selector = "h1.content-title";
	if ($(selector).length == 1) {
		$(selector).show();
	}
}

function pauseScrollLoadMore() {
	setTimeout(function() {
		isLoadingContent = false;
	}, 1000);
}

function closeSongPlaylist() {
	$(mashupMedia.songPlaylistSelector).slideUp('slow', function() {
		var imagePath = "<c:url value="${themePath}/images/controls/open.png" />";
		$("#current-song .toggle-playlist img").attr("src", imagePath);
	});			
}

function getTextFromField(textField) {
	if ($(textField).length = 0) {
		return "";
	}
	
	var text = $(textField).val();
	text = $.trim(text);
	return text;
}

function endsWith(text, suffix) {
    return text.indexOf(suffix, text.length - suffix.length) !== -1;
}

