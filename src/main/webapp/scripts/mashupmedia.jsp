<%@page contentType="text/javascript" %>

<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


var isLoadingContent = false;
var currentPage = "";

/*
var addressRandomAlbums = "address-random-albums";
var addressQuickSearchMediaItems = "address-quick-search-media-items";
var addressListArtists = "address-list-artists";
var addressListAlbums = "address-list-albums";
var addressListFilterAlbums = "address-filter-albums";
var addressFilterAlbumsByLetter = "address-filter-albums-letter-";
var addressAlbum = "address-load-album";
var addressArtist = "address-artist-";
var addressListPlaylists = "address-list-playlists";
var addressPlaylist = "address-playlist-";
var addressListPhotos = "address-list-photos";
*/

$(document).ready(function() {
    
	$("body, a").removeClass("cursor-progress");
		
	/*
	$("div.dynamic-content").off().on("click", "div.albums div.album-control a.play", function() {		
	    alert("play");
		var albumId = $(this).closest("div.album").attr("id");
		albumId = parseId(albumId, "album-id");
		mashupMedia.playAlbum(albumId);
	});

	$("div.dynamic-content").off().on("click", "div.albums div.album-control a.add", function() {
		var albumId = $(this).closest("div.album").attr("id");
		albumId = parseId(albumId, "album-id");
		mashupMedia.appendAlbum(albumId);
	});	
	*/
    
	$(document).ajaxComplete(function(e, xhr, settings) {
		var responseHtml = xhr.responseText;
		if (responseHtml.indexOf("@LOGGED-OUT@") >= 0) {
			window.location.reload();
		}		
	});
	
	
	var contextUrl = "<c:url value="/" />";
	mashupMedia.setContextUrl(contextUrl);
	
	/*
	$(".jp-previous").click(function() {
		mashupMedia.playPreviousSong();
	});
	$(".jp-next").click(function() {
		mashupMedia.playNextSong();
	});
	*/

	$("#log-out").click(function() {
		$("#form-log-out").submit(); 
	});
		
	


});


$(document).ajaxComplete(function() {
	$("body, a").removeClass("cursor-progress");
});

$(function () {
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
		xhr.setRequestHeader(header, token);
	});
});

	
	
	

var mashupMedia = new function() {
	this.contextUrl = $("#contextUrl").val();
	this.setContextUrl = function(contextUrl) {
		this.contextUrl = contextUrl;
	};
	this.isFfMpegInstalled = function() {
		var isInstalled = false;		
		$.ajax({
		  url: mashupMedia.contextUrl + "app/ajax/media/ffmpeg/status",
		  async: false,
		  dataType: "json",
		  success: function (json) {
		    isInstalled = json;
		  }
		});		
		return isInstalled;
	};
	this.songId = 0;
	//this.playingClass = "playing";
	this.jPlayerId = "#jquery_jplayer_1";
	//this.jPlayerContainerId = "#jp_container_1";
	this.filterPageNumber = 0;
	this.filterAlbumsSearchLetter = "";
	this.jPlayerSwfPath = "";
	
	this.showMessage = function(message) {
	    $("#information-box").html(message);
	    $("#information-box").show();
	    
	    setTimeout(function() {
	        $("#information-box").fadeOut("slow");                  
	    }, 10000);
	    
	};
	
	this.loadLastAccessedPlaylist = function() {	    
		$.get(mashupMedia.contextUrl + "app/restful/playlist/music/play/current", function(data) {		    
		    mashupMedia.prepareSong(data);
		});
	};
	
	this.loadPlaylist = function(playlistId) {
		$.get(mashupMedia.contextUrl + "app/ajax/playlist/play/id/" + playlistId, function(data) {
			var playlistId = data.mediaItem.playlistId;
			var mediaItemId = data.mediaItem.id;
			mashupMedia.loadSongFromPlaylist(playlistId, mediaItemId, false);
		});
	};
	
	this.playPlaylist = function(playlistId) {
		$.get(mashupMedia.contextUrl + "app/ajax/playlist/play/id/" + playlistId, function(data) {
			var playlistId = data.mediaItem.playlistId;
			var mediaItemId = data.mediaItem.id;
			mashupMedia.loadSongFromPlaylist(playlistId, mediaItemId, true);
		});
	};
	
//	this.playSong = function(song) {
//	    mashupMedia.streamSong(song);
//	    togglePlayPause("play");
//	    myAndroidFix.play();  
//	};

	/*
	this.loadSongFromPlaylist = function(playlistId, mediaItemId, isAutoPlay) {
		if (playlistId.length == 0 || isNaN(playlistId) || playlistId < 1) {
			return;
		}
		
		if (mediaItemId.length == 0 || isNaN(mediaItemId) || mediaItemId < 1) {
			$.get(mashupMedia.contextUrl + "app/ajax/playlist/id/" + playlistId, {
				"webFormatType" : "json",
				"updateLastAccessedToNow" : true
			}, function(data) {
				var playlistId = data.playlist.id;
				var playlistName = data.playlist.name;				
				mashupMedia.showSongInfo("", "", false, 0, 0, playlistName, playlistId, 0);
			});
			return;
		}
		
		
		$.get(mashupMedia.contextUrl + "app/ajax/music/play/media-item/" + mediaItemId, {
			"playlistId" : playlistId
		}, function(data) {
			$("#media-player-script").html(data);
			setupJPlayer(isAutoPlay);
		});
	};
	*/
	
	this.isMusicPlaying = function() {
	    if($(mashupMedia.jPlayerId) == false) {
	        return false;
	    }
	    
	    
	    if($(mashupMedia.jPlayerId).data().jPlayer.status.paused == false){
	       return true;
	    }
	    
	    return false;    
	}
	
	this.isMusicPlayerInitialised = function() {
	    if ($(mashupMedia.jPlayerId) == false) {
	        return false;
	    }
	    
	    var source = $(mashupMedia.jPlayerId).data().jPlayer.status.src;
	    return true;
	};
	
    this.prepareSong = function(song) {
        if (!song) {
            return;
        }                           

        var albumName = song.artistName + " - " + song.albumName;
        $("#music-player .album-art").html("<a rel=\"internal\" href=\"" + song.albumUrl + "\"><img title=\"" + albumName + "\" src=\"" + song.albumArtUrl + "\" /></a>");
        $("#music-player .artist-name").html("<a rel=\"internal\" href=\"" + song.artistUrl + "\">" + song.artistName + "</a>");        
        $("#music-player .title").text(song.title);
        mashupMedia.songId = song.id;
        setupJPlayer(song.streams);
        mashupMedia.showMusicPlayer(true);

    };
    
	this.streamSong = function(song) {
	    if (!song) {
	        return;
	    }	    	   	    
	    
	    mashupMedia.prepareSong(song);
        togglePlayPause("play");
        myAndroidFix.play();  

	};
	
	this.showMusicPlayer = function(isShow) {
	    if (isShow === true) {
	        $("#music-player").show();
	        $("#music-player").css("visibility", "visible");
	        $("#logo").hide();
	    } else {
            $("#music-player").hide();
            $("#music-player").css("visibility", "hidden");
            $("#logo").show();	        
	    }	    
	}

	this.playNextSong = function() {
	    $.get(mashupMedia.contextUrl + "app/restful/playlist/music/play/next", function(data) {       
	        mashupMedia.streamSong(data);	       
		});
	};

	this.playPreviousSong = function() {
        $.get(mashupMedia.contextUrl + "app/restful/playlist/music/play/previous", function(data) {       
            mashupMedia.streamSong(data);            
        });	    
	};

	
	this.showAlbum = function(albumId) {
		if(!isValidNumber(albumId)) {
			return;
		}
		
		$.get(mashupMedia.contextUrl + "app/ajax/music/album/" + albumId, function(data) {
			$("div.panel div.content").html(data);
		});
	};
	
    this.playSong = function(songId) {        
        $.get(mashupMedia.contextUrl + "app/restful/playlist/music/play-song", {
            "songId" : songId
        }, function(data) {
            mashupMedia.streamSong(data);
        });     
    };
	
	
	this.playArtist = function(artistId) {		
		$.get(mashupMedia.contextUrl + "app/restful/playlist/music/play-artist", {
			"artistId" : artistId
		}, function(data) {
		    mashupMedia.streamSong(data);
		});		
	};

	this.playAlbum = function(albumId) {
		$.get(mashupMedia.contextUrl + "app/restful/playlist/music/play-album", {
			"albumId" : albumId
		}, function(data) {		    
            mashupMedia.streamSong(data);
		});		
	};
	
	this.reinitialiseInfinitePage = function() {
	    window.scrollTo(0, 0);
	    this.filterPageNumber = 0;
	};
	
	/*
	this.playSong = function(songId) {
		$.get(mashupMedia.contextUrl + "app/ajax/playlist/play-song", {
			"songId" : songId
		}, function(data) {
			var mediaItemId = data.mediaItem.id;
			var playlistId = data.mediaItem.playlistId;
			mashupMedia.loadSongFromPlaylist(playlistId, mediaItemId, true);
		});
	};
	*/

    this.playSongSearchResults = function() {    	
		loadSearchResults(true);    
    };


	this.appendSongSearchResults = function() {	
		loadSearchResults(false);
	};

	
	this.appendArtist = function(artistId) {
		$.post(mashupMedia.contextUrl + "app/restful/playlist/music/append-artist", {
			"artistId" : artistId
		}, function(data) {
		});
	};

	this.appendAlbum = function(albumId) {
        $.get(mashupMedia.contextUrl + "app/restful/playlist/music/append-album", {
            "albumId" : albumId
        }, function(data) {            
        });     	    
	};
	
	this.appendSong = function(songId) {
		$.get(mashupMedia.contextUrl + "app/restful/playlist/music/append-song", {
			"songId" : songId
		}, function(data) {
		});		
	};

	this.destroyPlayer = function() {
		$(mashupMedia.jPlayerId).jPlayer("destroy");
	};
	
	this.clearPlayer = function() {
		mashupMedia.destroyPlayer();	
		mashupMedia.showEmptySongInfo();
	};
	
}

var myAndroidFix = null;

function setupJPlayer(streams) {        
    var streamFormats = "";
    var media = {};
    
    for (i = 0; i < streams.length; i++) {
        media[streams[i].format] = streams[i].url;
        
        if (i > 0) {
            streamFormats += ",";
        } 
        streamFormats += streams[i].format; 
    }
    
    console.log(media);

    if (myAndroidFix) {
        var isPlaying = mashupMedia.isMusicPlaying();
        myAndroidFix.setMedia(media);
        if (isPlaying) {
            myAndroidFix.play();
        }        
        return;
    }
    
    var options = {
        ready: function(event) {
            myAndroidFix.setMedia(media);
        },
        ended: function(event) {
            mashupMedia.playNextSong(true);
        },
        swfPath: mashupMedia.jPlayerSwfPath,
        supplied: streamFormats,
        cssSelectorAncestor: "#music-player",
        cssSelector: {
            title: ".information span.title",
            play: ".controls a.play",
            pause: ".controls a.pause",
            seekBar: "div.progress",
            playBar: "div.play-bar"
        },
        volume: 1,
        error: function(event) {
            console.log(event);
            $.post("<c:url value="/app/restful/encode/song" />", { id: mashupMedia.songId })
                .done(function( data ) {
                    mashupMedia.showMessage(data);          
            });                   
        }
    };
    
    myAndroidFix = new jPlayerAndroidFix(mashupMedia.jPlayerId, media, options);        
}





function showSongInPlaylistIfEmpty(playlistId, mediaItemId) {
	var currentSongId = $("#current-song-id").val();
	if (currentSongId > 0) {
		return;
	}
	
	mashupMedia.loadSongFromPlaylist(playlistId, mediaItemId, false);
	
} 

function updatePlaylistView(mediaItemId) {	
	if ($("#playlist").length < 1) {
		return;
	}
	
	if (mediaItemId == 0) {
	    return;
	}
	
	
	$("#playlist table.songs tbody tr").removeClass(mashupMedia.playingClass);
	
	$('#playlist table.songs tbody tr').each(function() {
		var rowId = $(this).attr("id");
		var rowMediaItemId = parseId(rowId, "media-id");
		if (mediaItemId == rowMediaItemId) {
			$(this).addClass(mashupMedia.playingClass);
			return;
		}
		
	});
	
}

function loadLink(path) {
	var windowLocation = window.location;
	windowLocation = windowLocation.replace()
	
	window.location = window.location + "#" + path;
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

function loadAlbums(viewType) { 
    if (isLoadingContent) {
        return;
    }
    isLoadingContent = true;
    mashupMedia.filterPageNumber++;
    
    var url = mashupMedia.contextUrl + "app/music/albums";
    if (viewType == 'latest') {
        url = mashupMedia.contextUrl + "app/music/latest-albums"
    } else if (viewType == 'random') {
        url = mashupMedia.contextUrl + "app/music/random-albums"
    }
    
    
    $.get(url, {
        append: true,
        fragment: true,
        pageNumber : mashupMedia.filterPageNumber,
        searchLetter: mashupMedia.filterAlbumsSearchLetter
    }, function(data) {        
        var albums = $(data).filter("div.albums");
        $("div.dynamic-content div.albums").append(albums);
        pauseScrollLoadMore();          
    });
}


function loadSongSearchResults(isAppend) {
	if (isLoadingContent) {
		return;
	}
	
	isLoadingContent = true;

	var serialisedSearchForm = $("#quick-search").serialize();
	
	$.post(mashupMedia.contextUrl + "app/ajax/search/media-items?" + serialisedSearchForm, {
		"pageNumber" : mashupMedia.filterPageNumber,
		"isAppend" : isAppend
	},	function(data) {
		if (isAppend) {
			$("div.panel div.content").append(data);	
		} else {
			$("div.panel div.content").html(data);
		}				
		pauseScrollLoadMore();		
	});				

}

function loadLatestPhotos(isAppend) {
	if (isLoadingContent) {
		return;
	}
	
	$.get("<c:url value="/app/ajax/photo/load-latest-photos" />", { pageNumber: mashupMedia.filterPageNumber }, function( data ) {
		$("body.photo div.sub-panel ul.photo-thumbnails").append( data );
		pauseScrollLoadMore();
	});
	
}


function loadArtists() {
	$.get("<c:url value="/app/ajax/music/artists" />", function(data) {
		$("div.panel div.content").html(data);
	});
}

function loadPlaylists() {
	$.get("<c:url value="/app/ajax/playlist/list" />", {
		"playlistType" : "music"
	}, function(data) {
		$("div.panel div.content").html(data);
	});
}

function loadPlaylist(playlistId) {
	$.get("<c:url value="/app/ajax/playlist/id/" />" + playlistId, function(data) {
		$("div.panel div.content").html(data);
	});
}

function loadArtist(artistId) {
	$.get("<c:url value="/app/ajax/music/artist/" />" + artistId, function(data) {
		$("div.panel div.content").html(data);
	});
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

function isValidNumber(value) {
	if (value.length == 0 || isNaN(value)) {
		return false;
	}
	
	return true;
}

function appendContentsOnScroll(contentType) {
    if ($(window).scrollTop() >= $(document).height() - $(window).height()) {
        
//		var pageNumber = mashupMedia.filterPageNumber + 1;
//		mashupMedia.filterPageNumber = pageNumber;
		
		if (contentType == "music-random-albums") {
		    loadAlbums("random");
		} else if (contentType == "music-latest-albums") {
		    loadAlbums("latest");
		} else if (contentType == "music-alphabetical-albums") {
		    loadAlbums("alphabetical");
		} else if (textStartsWith(currentPage, addressQuickSearchMediaItems)) {
		    loadSongSearchResults(true);    
		}  else if (textStartsWith(currentPage, addressListPhotos)) {
			loadLatestPhotos(true);			
		} 		
		
    }
}

function getURLParameter(name) {
    return decodeURIComponent(
        (location.search.match(RegExp("[?|&]"+name+'=(.+?)(&|$)'))||[,null])[1]
    );  
}


function getHostUrl() {
 	var protocol = window.location.protocol;
	var hostUrl = protocol;
	hostUrl += "//";
	hostUrl += window.location.hostname;
	
	var port = window.location.port;
	var portUrl = "";
	
	
	if (protocol == "http:" && port != 80) {
	    portUrl = ":" + port;
	} else if (protocol == "https:" && port != 443) {
	    portUrl = ":" + port;	    
	}
	
	hostUrl += portUrl;
	hostUrl += mashupMedia.contextUrl;
	return hostUrl;
}

function loadSearchResults(isReplace) {
	if ($("ul.search-results li").length == 0) {
    	return;
    }
    	
    var playlistId = $("#current-playlist-id").val();
    var mediaItemIds = [];
    
    $("ul.search-results li").each(function( index ) {
		var listId = $(this).attr("id");
		var mediaItemId = parseId(listId, "media-item-id");
		mediaItemIds.push(mediaItemId);			
	});
	
	var url = "app/ajax/playlist/append-media-items";
	if (isReplace) {
		url = "app/ajax/playlist/replace-media-items";
	}
	
		
	$.post(mashupMedia.contextUrl + url, {
   	    "mediaItemIds" : mediaItemIds
   	}, function(data) {
		var mediaItemId = data.mediaItem.id;
		var playlistId = data.mediaItem.playlistId;
		mashupMedia.loadSongFromPlaylist(playlistId, mediaItemId, true);
   	});	 	
}

function submitAjaxForm(formElement, pushTitle, pushUrl) {
	var formAction = formElement.attr("action");
	var formData = formElement.serialize();
	$.post(formAction, formData, function(data) {
	    var uiContentElement = $("div.ui-content");
	    uiContentElement.html(data);
	    uiContentElement.enhanceWithin();
	    if (data.indexOf("@IS_SHOWN_AFTER_FORM@") > -1) {
	    	History.pushState({pageType: "internal"}, pushTitle, pushUrl);
	    }
	    window.scrollTo(0, 0);
	});
}
	
function loadInternalPage(title, url) {    
    History.pushState({
        pageType: "internal"
    }, title, url);
    
    url = prepareInternalUrlFragment(url);
    
    $.get(url, function(data) {
        var uiContentElement = $("div.ui-content div.dynamic-content");
        uiContentElement.html(data);
        uiContentElement.enhanceWithin();
        
    });    
    
}
	
