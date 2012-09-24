<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<script type="text/javascript">
	function playSong() {

		$("#jquery_jplayer_1").jPlayer({
			ready: function (event) {
				$(this).jPlayer("setMedia", {
					<c:out value="${format}" /> : "<c:url value="/app/streaming/media/${mediaItemId}" />"  
				});
			},
			swfPath: "http://www.jplayer.org/latest/js/Jplayer.swf",
			supplied: "mp3",
			solution: "html, flash",
			wmode: "window"
//		errorAlerts: true
		});	
	}

</script>







