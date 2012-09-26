<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>




<script type="text/javascript">
	function playSong() {
		$(mashupMedia.jPlayerId).jPlayer({
			ready: function (event) {
				$(this).jPlayer("setMedia", {
					<c:out value="${format}" /> : "<c:url value="/app/streaming/media/${mediaItemId}" />"  
				});
			},
			swfPath: "http://www.jplayer.org/latest/js/Jplayer.swf",
			supplied: "mp3, ogg",
			solution: "html, flash",
			wmode: "window",
			ended: function() { 
				mashupMedia.playNextSong();				
			},
			errorAlerts: true
		})
		;	
		
		
		/*
		$(mashupMedia.jPlayerId).bind($.jPlayer.event.durationchange, function(event) { // Add a listener to report the time play began
			alert('durationchange');
		//	$("#playBeganAtTime").text("Play began at time = " + event.jPlayer.status.currentTime);
		});
*/
		
	}

</script>







