<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>




<script type="text/javascript">
	function playSong() {		
		
		$(mashupMedia.jPlayerId).jPlayer({
			ready: function (event) {
				$(this).jPlayer("setMedia", {
					<c:out value="${format}" /> : "<c:url value="/app/streaming/media/${mediaItemId}" />"  
				});
				$(this).bind($.jPlayer.event.play,
			                function(event) {
//						alert("can play");
//						$(mashupMedia.jPlayerId).find(".jp-seek-bar").css("width", "100%");
						/*
			                    if (event.jPlayer.status.seekPercent === 100) {
			                        jPlayerElement.jPlayer("play");
			                    }
						*/
			                    /*
			                    if (actionAfterMusic) {
			                        actionAfterMusic.call();
			                    }
			                    */
			                });

			},
			swfPath: "http://www.jplayer.org/latest/js/Jplayer.swf",
			supplied: "mp3, ogg",
			solution: "html, flash",
			wmode: "window",
			ended: function() { 
				mashupMedia.playNextSong();				
			},
			preload: "auto"
	//		errorAlerts: true
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







