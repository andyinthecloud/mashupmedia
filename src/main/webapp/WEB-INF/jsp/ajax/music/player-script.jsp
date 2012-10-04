<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>




<script type="text/javascript">
	function playSong() {
		
		var playingRow = getPlayingRow();
		if ($(playingRow).length == 0) {
			return;
		}
		
		var rowId = $(playingRow).attr("id");
		//var mediaItemId = parseId(rowId, "playlist-media-id");
		var albumId = parseId(rowId, "album-id");
		
		var songTitle = $(playingRow).find("td.song-title").text();					
		$("#current-song .song-title").text(songTitle);	
		$("#current-song .vote").show();
		$("#current-song .album-art").attr("src", "/mashupmedia/app/music/album-art/" + albumId);
		
		$(mashupMedia.jPlayerId).jPlayer({
			ready: function (event) {
				$(this).jPlayer("setMedia", {
					<c:out value="${format}" /> : "<c:url value="/app/streaming/media/${mediaItemId}" />"  
				});
			},
			swfPath: "<c:url value="/jquery-plugins/jquery.jplayer/2.2.0" />",
			supplied: "mp3, ogg",
			solution: "html, flash",
			wmode: "window",
			ended: function() { 
				mashupMedia.playNextSong();				
			},
			preload: "auto"
	//		errorAlerts: true
		});	
		
		
		/*
		$(mashupMedia.jPlayerId).bind($.jPlayer.event.durationchange, function(event) { // Add a listener to report the time play began
			alert('durationchange');
		//	$("#playBeganAtTime").text("Play began at time = " + event.jPlayer.status.currentTime);
		});
*/
		
	}

</script>







