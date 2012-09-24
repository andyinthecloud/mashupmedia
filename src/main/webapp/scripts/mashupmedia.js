
$(document).ready(function() {
	var contextUrl = $("#contextUrl").val();
	mashupMedia.setContextUrl(contextUrl);
});

var mashupMedia = new function() {
	this.contextUrl =  $("#contextUrl").val(); 
	this.setContextUrl = function(contextUrl) {
		this.contextUrl = contextUrl;
	};
	this.loadPlaylist = function() {
//		alert(this.contextUrl);
		var contextUrl = this.contextUrl;
		
		$.post(contextUrl + "app/ajax/playlist/current-user-playlist",
				function(data) {
					$("#top-bar-music-player .songs").html(data);
					
					/*
					
					$("#top-bar-music-player table.song-playlist tbody tr")
							.each(function(index) {
//								var format = $(this).find("input[name=format]").val();								
								var format = "mp3";								
								var playlistRowId = $(this).attr("id");
								var mediaId = getMediaIdFromPlaylist(playlistRowId);
								var mediaUrl = this.contextUrl + "/app/streaming/media" + mediaId;
								var playlistItem = new PlaylistItem(format, mediaUrl);
								playlistItems.push(playlistItem);

							});
					*/
					
//					loadCurrentSong();
					
					var playingRow = $("#top-bar-music-player .songs table tbody tr.playing");
					
					if ($(playingRow).length == 0) {
						return;
					}
					
					var songTitle = $(playingRow).find("td.song-title").text();
					
					var currentTrackDisplay = songTitle;
					$("#current-song .song-title").text(currentTrackDisplay);	
					$("#current-song .vote").show();
					
					var mediaId = $(playingRow).attr("id").replace("playlist-media-id-", "");
					
					$.get(contextUrl + "app/ajax/music/play/" + mediaId,
						function(data) {
							$("#media-player-script").html(data);
							playSong();
					});

					
				});
	};
	this.loadCurrentSong = function() {
		var playingRow = $("#top-bar-music-player .songs table tbody tr.playing");
		
		if ($(playingRow).length == 0) {
			return;
		}
		
		var songTitle = $(playingRow).find("td.song-title").text();
		
		var currentTrackDisplay = songTitle;
		$("#current-song .song-title").text(currentTrackDisplay);	
		$("#current-song .vote").show();
		
		var mediaId = $(playingRow).attr("id").replace("playlist-media-id-", "");
		
		$.get(this.contextUrl + "/app/ajax/music/play/" + mediaId,
			function(data) {
				$("#media-player-script").html(data);
				playSong();
		});
		
//		playSong(mediaId);
		
	};
}


function getMediaIdFromPlaylist(playlistRowId) {
	var mediaId = $(playlistRowId).attr("id").replace("playlist-media-id-", "");
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