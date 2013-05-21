<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>



<c:url var="streamingUrl" value="/app/streaming/media/unprocessed/${song.id}"/>
<c:set var="streamingFormat" value="${song.mediaContentType}"/>
<c:if test="${song.encodeStatusTypeValue == 'PROCESSING' || song.encodeStatusTypeValue == 'ENCODED'}">
	<c:url var="streamingUrl" value="/app/streaming/media/encoded/${song.id}"/>
	<c:set var="streamingFormat" value="oga"/>
</c:if>


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

		mashupMedia.showSongInfo("${song.displayTitle}", "${song.artist.name}", true, ${song.album.id}, ${song.id}, "${playlist.name}", ${playlist.id}, "${song.encodeStatusTypeValue}");
		
		
		var jPlayerStatus = "load";
		if (isAutoPlay) {
			jPlayerStatus = "play";
		}
		
		
		
		$(mashupMedia.jPlayerId).jPlayer({
			ready: function (event) {
				$(this).jPlayer("setMedia", {
					${streamingFormat} : "${streamingUrl}"
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
//			errorAlerts: true
/*
			error: function (event) {
		        console.log(event.jPlayer.error);
		        console.log(event.jPlayer.error.type);
			}			
*/			
		});	
		
		
	}

</script>







