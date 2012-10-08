<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>




<script type="text/javascript">
	function setupJPlayer(isAutoPlay) {
		
		var playingRow = getPlayingRow();
		if ($(playingRow).length == 0) {
			return;
		}
				
		
		$(mashupMedia.jPlayerId).jPlayer("destroy");
		
		var rowId = $(playingRow).attr("id");
		//var mediaItemId = parseId(rowId, "playlist-media-id");
		var albumId = parseId(rowId, "album-id");
		
		var albumUrl = "<c:url value="/app/ajax/music/album/" />" + albumId;
		
		var songTitle = $(playingRow).find("td.song-title").text();					
		$("#current-song .song-title").text(songTitle);	
		$("#current-song .vote").show();
		$("#current-song .album-art img").attr("src", "/mashupmedia/app/music/album-art/" + albumId);
		
		var jPlayerStatus = "load";
		if (isAutoPlay) {
			jPlayerStatus = "play";
		}
		
		$(mashupMedia.jPlayerId).jPlayer({
			ready: function (event) {
				$(this).jPlayer("setMedia", {
					<c:out value="${format}" /> : "<c:url value="/app/streaming/media/${mediaItemId}" />"  
				}).jPlayer(jPlayerStatus);
				
			},
			swfPath: "<c:url value="/jquery-plugins/jquery.jplayer/2.2.0" />",
			supplied: "mp3, ogg",
			solution: "html, flash",
			wmode: "window",
			ended: function() { 
				mashupMedia.playNextSong();				
			},
			preload: "auto",
			errorAlerts: true
		});	
		
		
	}

</script>







