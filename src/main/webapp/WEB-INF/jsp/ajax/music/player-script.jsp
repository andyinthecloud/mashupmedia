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
		
		
		
//		var id = "#jquery_jplayer_1";

		var bubble = {
			${streamingFormat}: "<c:url value="${streamingUrl}" />"
		};
		/*
		var lismore = {
			mp3:"http://www.jplayer.org/audio/mp3/Miaow-04-Lismore.mp3",
			oga:"http://www.jplayer.org/audio/ogg/Miaow-04-Lismore.ogg"
		};
		*/

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
			swfPath: "<c:url value="/jquery-plugins/jquery.jplayer/${jPlayerVersion}" />",
			supplied: "${streamingFormat}",
			wmode: "window",
			smoothPlayBar: true,
			keyEnabled: true,
			ended: function() { 
				mashupMedia.playNextSong();				
			},			
			preload: "auto"
		};

		var myAndroidFix = new jPlayerAndroidFix(mashupMedia.jPlayerId, bubble, options);
		
//		myAndroidFix.play();
//		myAndroidFix.setMedia(bubble).play()
		
		
		
		var albumUrl = "<c:url value="/app/ajax/music/album/${song.album.id}" />";
		
		mashupMedia.showSongInfo("${song.displayTitle}", "${song.artist.name}", true, ${song.album.id}, ${song.id}, "${playlist.name}", ${playlist.id}, ${song.artist.id});
		
		

		
		/*
		$(mashupMedia.jPlayerId).jPlayer({
			ready: function (event) {
				$(this).jPlayer("setMedia", {
					${streamingFormat} : "<c:url value="${streamingUrl}" />"
				}).jPlayer(jPlayerStatus);				
			},
			swfPath: "<c:url value="/jquery-plugins/jquery.jplayer/2.4.0" />",
			supplied: "${streamingFormat}",
			solution: "html, flash",
			wmode: "window",
			ended: function() { 
				mashupMedia.playNextSong();				
			},
			preload: "auto"
		});	
		*/
				
		
	}

</script>







