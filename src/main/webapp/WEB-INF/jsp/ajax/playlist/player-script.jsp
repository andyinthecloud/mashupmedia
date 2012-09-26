<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

html


<script type="text/javascript">
	function playSong() {
		/*
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
		});	
		*/
		
		var jPlayerId = mashupMedia.jPlayerId;
		var jPlayerContainerId = mashupMedia.jPlayerContainerId;
		
		new jPlayerPlaylist({
			jPlayer: "#jquery_jplayer_1",
			cssSelectorAncestor: "#jp_container_1"			
		}, [
		    
		    
			<c:forEach items="${mediaItems}" var="mediaItem" varStatus="status">
			<c:if test="${!status.first}">,</c:if>
			{
				title: "<c:out value="${mediaItem.fileName}" />",
				mp3: "<c:url value="/app/streaming/media/${mediaItem.id}" />"
			}
			</c:forEach>

		], {
//			swfPath: "../js",
			swfPath: "http://www.jplayer.org/latest/js/Jplayer.swf",
			supplied: "mp3, ogg",
			solution: "html, flash",
			wmode: "window",
			errorAlerts: true
		});		
		
		jp-duration
		
		$("#jquery_jplayer_1 .jp-duration").text("05:10");
		
	}

</script>







