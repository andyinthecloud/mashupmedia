<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>




<script type="text/javascript">
	$(document).ready(function() {
		
	    $('#current-song .album-art a').address(function() {
			var albumImageSrc = $(this).find("img").attr("src");
			var albumId = albumImageSrc.replace(/.*album-art\//, "");
			var addressValue = "address-load-album-" + albumId;
	    	$.address.value(addressValue);  
	    });
	    
	});

	function setupJPlayer(isAutoPlay) {
		/*
		var playingRow = getPlayingRow();
		if ($(playingRow).length == 0) {
			return;
		}
		*/

		
		$(mashupMedia.jPlayerId).jPlayer("destroy");
		
		/*
		var rowId = $(playingRow).attr("id");		
		var albumId = parseId(rowId, "album-id");
		*/
		
		var albumUrl = "<c:url value="/app/ajax/music/album/${song.album.id}" />";
	
		/*
		var songTitle = $(playingRow).find("td.song-title").text();
		var artistName = $(playingRow).find("td.artist-name").text();
		*/
		
		mashupMedia.showSongInfo("${song.displayTitle}", "${song.artist.name}", true, ${song.artist.id}, ${song.id});
		
		
		var jPlayerStatus = "load";
		if (isAutoPlay) {
			jPlayerStatus = "play";
		}
		
		$(mashupMedia.jPlayerId).jPlayer({
			ready: function (event) {
				$(this).jPlayer("setMedia", {
					<c:out value="${format}" /> : "<c:url value="/app/streaming/media/${song.id}" />"  
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







