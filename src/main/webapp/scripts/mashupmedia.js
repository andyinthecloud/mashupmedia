
$(document).ready(function() {
	var contextUrl = $("#contextUrl").val();
	mashupMedia.setContextUrl(contextUrl);
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
		var contextUrl = this.contextUrl;
		
		$.post(contextUrl + "app/ajax/playlist/current-user-playlist",
				function(data) {
					$("#top-bar-music-player .songs").html(data);					
					
					var playingRow = getPlayingRow();					
					if ($(playingRow).length == 0) {
						return;
					}
					
					var songTitle = $(playingRow).find("td.song-title").text();
					
					var currentTrackDisplay = songTitle;
					$("#current-song .song-title").text(currentTrackDisplay);	
					$("#current-song .vote").show();
					
					var mediaId = $(playingRow).attr("id").replace("playlist-media-id-", "");
					//var playlistId = $("#current-playlist-id").val();
					
					$.get(contextUrl + "app/ajax/music/play/" + mediaId,
						function(data) {
							$("#media-player-script").html(data);
							playSong();
					});

					
				});
	};
	this.loadCurrentSong = function() {
		var playingRow = getPlayingRow();
		if ($(playingRow).length == 0) {
			return;
		}
		
		var songTitle = $(playingRow).find("td.song-title").text();
		
		var currentTrackDisplay = songTitle;
		$("#current-song .song-title").text(currentTrackDisplay);	
		$("#current-song .vote").show();
		
		//var mediaId = $(playingRow).attr("id").replace("playlist-media-id-", "");
		var mediaId = getMediaIdFromPlaylistRow(playlistRow);
		
		$.get(this.contextUrl + "/app/ajax/playlist/id/" + mediaId,
			function(data) {
				$("#media-player-script").html(data);
				playSong();
				$(jPlayerId).jPlayer("load");
		});
		
//		playSong(mediaId);
		
	};
	
	this.playNextSong = function() {
		var playingRow = getPlayingRow();
		if ($(playingRow).length == 0) {
			return;
		}
		
//		$(playingRow).removeClass(this.playingClass);		
		var playingIndex = $("#top-bar-music-player .songs table tbody tr").index(playingRow);
		
		var nextPlayingRow =  $("#top-bar-music-player .songs table tbody tr")[playingIndex + 1];
		if ($(nextPlayingRow).length == 0) {
			return;
		}
		
		$(playingRow).removeClass(this.playingClass);
		$(nextPlayingRow).addClass(this.playingClass);
		var mediaId = getMediaIdFromPlaylistRow(nextPlayingRow);		
		var jPlayerId = this.jPlayerId;
		
		$.get(this.contextUrl + "/app/ajax/music/play/" + mediaId,
			function(data) {
				$("#media-player-script").html(data);
				$(jPlayerId).jPlayer("destroy");
				playSong();
				$(jPlayerId).jPlayer("play");
		});
		
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


function getMediaIdFromPlaylistRow(playlistRow) {
	var mediaId = $(playlistRow).attr("id").replace("playlist-media-id-", "");
	return mediaId;
}

/*
function loadCurrentSong() {
	var playingRow = $("#top-bar-music-player .songs table tbody tr.playing");
	
	if ($(playingRow).length == 0) {
		return;
	}
	
	var songTitle = $(playingRow).find("td.song-title").text();
	
	var currentTrackDisplay = songTitle;
	$("#current-song .song-title").text(currentTrackDisplay);	
	$("#current-song .vote").show();
	
	var mediaId = $(playingRow).attr("id").replace("playlist-media-id-", "");
	playSong(mediaId);

}
*/