var isNextActionDelayed = false;
var currentPage = "";

$(document).ready(function() {
    
	$("body, a").removeClass("cursor-progress");
		
	$(document).ajaxComplete(function(e, xhr, settings) {
		var responseHtml = xhr.responseText;
		if (responseHtml.indexOf("@LOGGED-OUT@") >= 0) {
			window.location.reload();
		}		
	});
		
	$("#log-out").click(function() {
		$("#form-log-out").submit(); 
	});
		
	 setupJPlayer();
});


function bindEndedEventInMusicPlayer() {

    
}


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
	this.contextUrl = window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
	this.isFfMpegInstalled = function() {
		var isInstalled = false;		
		$.ajax({
		  url: mashupMedia.contextUrl + "/app/ajax/media/ffmpeg/status",
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
	    $("#information-box").delay(30000).fadeOut("slow");
	};
	
	this.loadLastAccessedPlaylist = function() {	    
		$.get(mashupMedia.contextUrl + "/app/restful/playlist/music/play/current", function(data) {		    
		    mashupMedia.prepareSong(data);
		});
	};
	
	this.loadPlaylist = function(playlistId) {
		$.get(mashupMedia.contextUrl + "/app/ajax/playlist/play/id/" + playlistId, function(data) {
			var playlistId = data.mediaItem.playlistId;
			var mediaItemId = data.mediaItem.id;
			mashupMedia.loadSongFromPlaylist(playlistId, mediaItemId, false);
		});
	};
	
	this.playPlaylist = function(playlistId) {
		$.get(mashupMedia.contextUrl + "/app/ajax/playlist/play/id/" + playlistId, function(data) {
			var playlistId = data.mediaItem.playlistId;
			var mediaItemId = data.mediaItem.id;
			mashupMedia.loadSongFromPlaylist(playlistId, mediaItemId, true);
		});
	};
	

	
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
        playMusic(song.streams);
        mashupMedia.showMusicPlayer(true);

    };
    
	this.streamSong = function(song) {
	    if (!song) {
	        return;
	    }    	   	    
	    	    
	    mashupMedia.prepareSong(song);
        togglePlayPause("play");
        $(mashupMedia.jPlayerId).jPlayer("load");
        $(mashupMedia.jPlayerId).jPlayer("play");

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

	this.playCurrentSong = function() {
	    $.ajax({
            method: "GET",
            url: mashupMedia.contextUrl + "/app/restful/playlist/music/play/current",
            async: false
        }).done(function(data) {
            mashupMedia.streamSong(data);
        });	    
	};
	    
	this.playNextSong = function() {
        $.ajax({
            method: "GET",
            url: mashupMedia.contextUrl + "/app/restful/playlist/music/play/next",
            async: false
        }).done(function(data) {
            mashupMedia.streamSong(data);
        });     
	    
	};
	
	this.playPreviousSong = function() {
        $.ajax({
            method: "GET",
            url: mashupMedia.contextUrl + "/app/restful/playlist/music/play/previous",
            async: false
        }).done(function(data) {
            mashupMedia.streamSong(data);
        });     

	};

	

	
    this.playSong = function(songId) {        
        $.get(mashupMedia.contextUrl + "/app/restful/playlist/music/play-song", {
            "songId" : songId
        }, function(data) {
            mashupMedia.streamSong(data);
        });     
    };
	
	
	this.playArtist = function(artistId) {		
		$.get(mashupMedia.contextUrl + "/app/restful/playlist/music/play-artist", {
			"artistId" : artistId
		}, function(data) {
		    mashupMedia.streamSong(data);
		});		
	};

	this.playAlbum = function(albumId) {
		$.get(mashupMedia.contextUrl + "/app/restful/playlist/music/play-album", {
			"albumId" : albumId
		}, function(data) {		    
            mashupMedia.streamSong(data);
		});		
	};
	
	this.reinitialiseInfinitePage = function() {
	    window.scrollTo(0, 0);
	    this.filterPageNumber = 0;
	};
	
    this.playSongSearchResults = function() {    	
		loadSearchResults(true);    
    };


	this.appendSongSearchResults = function() {	
		loadSearchResults(false);
	};

	
	this.appendArtist = function(artistId) {
		$.post(mashupMedia.contextUrl + "/app/restful/playlist/music/append-artist", {
			"artistId" : artistId
		}, function(data) {
		});
	};

	this.appendAlbum = function(albumId) {
        $.get(mashupMedia.contextUrl + "/app/restful/playlist/music/append-album", {
            "albumId" : albumId
        }, function(data) {            
        });     	    
	};
	
	this.appendSong = function(songId) {
		$.get(mashupMedia.contextUrl + "/app/restful/playlist/music/append-song", {
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


function playMusic(streams) {
    var media = {};
            
    for (i = 0; i < streams.length; i++) {
        media[streams[i].format] = streams[i].url;
    }
    
    //myAndroidFix.setMedia(media);
    $(mashupMedia.jPlayerId).jPlayer("setMedia", media);
    if (mashupMedia.isMusicPlaying()) { 
        //myAndroidFix.play();
        $(mashupMedia.jPlayerId).jPlayer("play");
    }
}

//var isJPlayerInitialised = false;
//var myAndroidFix = null;
function setupJPlayer() {
    var options = {
        ready: function(event) {
            
        },
        ended:  function(event) {
        	//playMusic(streamFormats, media);
            var media = event.jPlayer.status.media;
            var mp3 = media.mp3;
            if (mp3 && mp3.indexOf("silent.mp3") > -1) {
                mashupMedia.playCurrentSong();
            } else {
                mashupMedia.playNextSong();
            }                                        
        },
        swfPath: mashupMedia.jPlayerSwfPath,
        supplied: "mp3",
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
            togglePlayPause("stop");
            $.post(mashupMedia.contextUrl + "/app/restful/encode/playlist", { mediaItemId: mashupMedia.songId })
                .done(function( data ) {
                    mashupMedia.showMessage(data);
                    $(mashupMedia.jPlayerId).jPlayer("play");
            });                   
        }
    };    
                    
    
    var media = {
       mp3: mashupMedia.contextUrl + "/jquery-plugins/jquery.jplayer/silent.mp3"
    };
    
    $(mashupMedia.jPlayerId).jPlayer(options);
    $(mashupMedia.jPlayerId).jPlayer("setMedia", media);
    $(mashupMedia.jPlayerId).jPlayer("play");
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
    if (this.isNextActionDelayed) {
        return;
    }
    this.isNextActionDelayed = true;
    mashupMedia.filterPageNumber++;
    
    var url = mashupMedia.contextUrl + "/app/music/albums";
    if (viewType == 'latest') {
        url = mashupMedia.contextUrl + "/app/music/latest-albums"
    } else if (viewType == 'random') {
        url = mashupMedia.contextUrl + "/app/music/random-albums"
    }
    
    
    $.get(url, {
        append: true,
        fragment: true,
        pageNumber : mashupMedia.filterPageNumber,
        searchLetter: mashupMedia.filterAlbumsSearchLetter
    }, function(data) {        
        $("div.dynamic-content div.albums").append(data);
        delayNextAction();          
    });
}

function loadPhotos(viewType) {
    if (viewType != "latest") {
        return;
    }
    
    if (this.isNextActionDelayed) {
        return;
    }
    this.isNextActionDelayed = true;
    mashupMedia.filterPageNumber++;
    
    var url = mashupMedia.contextUrl + "/app/photo/photos?order=" + viewType;;
    
    $.get(url, {
        append: true,
        fragment: true,
        pageNumber : mashupMedia.filterPageNumber,
        searchLetter: mashupMedia.filterAlbumsSearchLetter
    }, function(data) {
        $("div.dynamic-content ul.photos").append(data);
        delayNextAction();          
    });
}


function loadSongSearchResults(isAppend) {
	if (this.isNextActionDelayed) {
		return;
	}
	
	this.isNextActionDelayed = true;

	var serialisedSearchForm = $("#quick-search").serialize();
	
	$.post(mashupMedia.contextUrl + "/app/ajax/search/media-items?" + serialisedSearchForm, {
		"pageNumber" : mashupMedia.filterPageNumber,
		"isAppend" : isAppend
	},	function(data) {
		if (isAppend) {
			$("div.panel div.content").append(data);	
		} else {
			$("div.panel div.content").html(data);
		}				
		delayNextAction();		
	});				

}


function loadPlaylists() {
	$.get(mashupMedia.contextUrl + "/app/ajax/playlist/list", {
		"playlistType" : "music"
	}, function(data) {
		$("div.panel div.content").html(data);
	});
}

function loadPlaylist(playlistId) {
	$.get(mashupMedia.contextUrl + "/app/ajax/playlist/id/" + playlistId, function(data) {
		$("div.panel div.content").html(data);
	});
}



function delayNextAction() {
	setTimeout(function() {
		this.isNextActionDelayed = false;
	}, 1000);	
			
}

/*
function closeSongPlaylist() {
	$(mashupMedia.songPlaylistSelector).slideUp('slow', function() {
		var imagePath = "<c:url value="${themePath}/images/controls/open.png" />";
		$("#current-song .toggle-playlist img").attr("src", imagePath);
	});			
}
*/

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
    
    // add 50 pixels margin of error
    if ($(window).scrollTop() >= ($(document).height() - $(window).height() - 50)) {		
		if (contentType == "music-random-albums") {
		    loadAlbums("random");
		} else if (contentType == "music-latest-albums") {
		    loadAlbums("latest");
		} else if (contentType == "music-alphabetical-albums") {
		    loadAlbums("alphabetical");
		} else if (contentType == "photo-list-latest") {
            loadPhotos("latest");
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
	
	var url = "/app/ajax/playlist/append-media-items";
	if (isReplace) {
		url = "/app/ajax/playlist/replace-media-items";
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
//    url = prepareInternalUrlFragment(url);
    
    History.pushState({
        pageType: "internal"
    }, title, url);    
}
	
