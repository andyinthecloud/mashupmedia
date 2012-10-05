
$(document).ready(function() {
	var contextUrl = $("#contextUrl").val();
	mashupMedia.setContextUrl(contextUrl);
	$(".jp-play").click(function() {
		$(".jp-progress").css("width", "100%");
	});
	
	
});

var mashupMedia = new function() {
	this.contextUrl =  $("#contextUrl").val(); 
	this.setContextUrl = function(contextUrl) {
		this.contextUrl = contextUrl;
	};
	this.playingClass = "playing";
	this.jPlayerId = "#jquery_jplayer_1";
	this.jPlayerContainerId = "#jp_container_1";
	
	this.loadPlaylist = function() {		
		$.post(mashupMedia.contextUrl + "app/ajax/playlist/current-user-playlist",
				function(data) {
					$("#top-bar-music-player .songs").html(data);							
					mashupMedia.loadSong();
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
					setupJPlayer(true);
		});
				
	};
	
	
	this.playNextSong = function() {
		var playingRow = getPlayingRow();
		if ($(playingRow).length == 0) {
			return;
		}
		
		var playingIndex = $("#top-bar-music-player .songs table tbody tr").index(playingRow);
		
		var nextPlayingRow =  $("#top-bar-music-player .songs table tbody tr")[playingIndex + 1];
		if ($(nextPlayingRow).length == 0) {
			return;
		}
		
		$(playingRow).removeClass(this.playingClass);
		$(nextPlayingRow).addClass(this.playingClass);
		
		mashupMedia.loadSong(true);		
	};
}



function getPlayingRow() {
	var playingRow = $("#top-bar-music-player .songs table tbody tr." + mashupMedia.playingClass);
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
