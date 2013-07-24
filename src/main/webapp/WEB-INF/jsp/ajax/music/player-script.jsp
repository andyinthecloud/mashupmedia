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
	    	    
		$(mashupMedia.jPlayerId).jPlayer("destroy");		
		var albumUrl = "<c:url value="/app/ajax/music/album/${song.album.id}" />";
		
		var songEncodeStatusType = "NOT-INSTALLED";
		<c:if test="${isEncoderInstalled}" >
			songEncodeStatusType = "${song.encodeStatusTypeValue}";
		</c:if>

		mashupMedia.showSongInfo("${song.displayTitle}", "${song.artist.name}", true, ${song.album.id}, ${song.id}, "${playlist.name}", ${playlist.id}, songEncodeStatusType, ${song.artist.id});
		
		var jPlayerStatus = "load";
		if (isAutoPlay) {
			jPlayerStatus = "play";
		}
		
		$(mashupMedia.jPlayerId).jPlayer({
			ready: function (event) {
				$(this).jPlayer("setMedia", {
					${streamingFormat} : "<c:url value="${streamingUrl}" />"
				}).jPlayer(jPlayerStatus);				
			},
			swfPath: "<c:url value="/jquery-plugins/jquery.jplayer/2.3.0" />",
			supplied: "${streamingFormat}",
			solution: "html, flash",
			wmode: "window",
			ended: function() { 
				mashupMedia.playNextSong();				
			},
			preload: "auto"
		});	
		
		
	}

</script>







