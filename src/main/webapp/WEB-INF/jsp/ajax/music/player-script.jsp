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
	var bubble = {
		${streamingFormat}: "<c:url value="${streamingUrl}" />"
	};

	var jPlayerStatus = "load";
	if (isAutoPlay) {
		jPlayerStatus = "play";
	}
	
	var options = {			
		ready: function (event) {
			myAndroidFix.setMedia(bubble);
			if (isAutoPlay) {
				myAndroidFix.play();
			}				
		},			
		swfPath: "<c:url value="/jquery-plugins/jquery.jplayer/${jPlayerVersion}/jplayer" />",
		supplied: "${streamingFormat}",
		wmode: "window",
		useStateClassSkin: true,
		autoBlur: false,
		smoothPlayBar: true,
		keyEnabled: true,
		remainingDuration: true,
		toggleDuration: true,
		ended: function() { 
			mashupMedia.playNextSong();				
		},			
		preload: "auto"
	};
	
	var myAndroidFix = new jPlayerAndroidFix(mashupMedia.jPlayerId, bubble, options);		
	var albumUrl = "<c:url value="/app/ajax/music/album/${song.album.id}" />";		
	mashupMedia.showSongInfo("${song.displayTitle}", "${song.artist.name}", true, ${song.album.id}, ${song.id}, "${playlist.name}", ${playlist.id}, ${song.artist.id});
	
}

</script>







