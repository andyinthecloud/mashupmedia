<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div id="media-player-script">
</div>


<script type="text/javascript">
	$(document).ready(function() {

		$("#current-song .toggle-playlist").click(function() {
			$("#top-bar-music-player .songs").toggle("slow");
		});
		
		/*
		$("#jquery_jplayer_1").jPlayer({
			ready: function (event) {
				$(this).jPlayer("setMedia", {
//					mp3: "http://localhost:8080/mashupmedia/app/streaming/media/44/song.mp3",
					mp3: "/mashupmedia/app/streaming/media/44"
				});
			},
			swfPath: "http://www.jplayer.org/latest/js/Jplayer.swf",
			supplied: "mp3",
			solution: "html, flash",
			wmode: "window",
			errorAlerts: true
		});
		*/
		

	});
</script>


<div id="top-bar-music-player" class="top-bar">

	<ul class="main-menu group">
		<li><a href="#">User</a></li>
		<li><a href="#"><spring:message code="top-bar.now-playing" /></a></li>
		<li><a href="#">Sunny</a></li>
		<li><a href="#"><spring:message code="top-bar.log-out" /></a></li>
	</ul>

	<div class="clear"></div>

	<input type="hidden" id="playlist-id" /> <input type="hidden" id="playlist-isDefault" />

	<div id="jquery_jplayer_1" class="jp-jplayer"></div>
	<div id="jp_container_1" class="jp-audio">
		<div class="jp-type-single">
			<div class="jp-gui jp-interface">
				<ul class="jp-controls">
					<li><a href="javascript:;" class="jp-play" tabindex="1">play</a></li>
					<li><a href="javascript:;" class="jp-pause" tabindex="1">pause</a></li>
					<li><a href="javascript:;" class="jp-stop" tabindex="1">stop</a></li>
					<li><a href="javascript:;" class="jp-mute" tabindex="1" title="mute">mute</a></li>
					<li><a href="javascript:;" class="jp-unmute" tabindex="1" title="unmute">unmute</a></li>
					<li><a href="javascript:;" class="jp-volume-max" tabindex="1" title="max volume">max
							volume</a></li>
				</ul>
				<div class="jp-progress">
					<div class="jp-seek-bar">
						<div class="jp-play-bar"></div>
					</div>
				</div>
				<div class="jp-volume-bar">
					<div class="jp-volume-bar-value"></div>
				</div>
				<div class="jp-time-holder">
					<div class="jp-current-time"></div>
					<div class="jp-duration"></div>
					<ul class="jp-toggles">
						<li><a href="javascript:;" class="jp-repeat" tabindex="1" title="repeat">repeat</a></li>
						<li><a href="javascript:;" class="jp-repeat-off" tabindex="1" title="repeat off">repeat
								off</a></li>
					</ul>
				</div>
			</div>
			<div class="jp-title">



			</div>
			<div class="jp-no-solution">
				<span>Update Required</span> To play the media you will need to either update your browser to a
				recent version or update your <a href="http://get.adobe.com/flashplayer/" target="_blank">Flash
					plugin</a>.
			</div>
		</div>
	</div>

	<div id="current-song">
		<input type="hidden" id="current-song-id" value="" /> <img class="album-art"
			src="<c:url value="/images/no-album-art.png" />" /> <span class="song-title"><spring:message
				code="music.playlist.current-song.empty" /></span> <span class="vote"> <a
			href="javascript:void(0);"
			title="<spring:message code="music.playlist.current-song.vote.love" />"><img
				src="<c:url value="${themePath}/images/controls/thumbs-up.png"/>" /></a> <a
			href="javascript:void(0);"><img
				src="<c:url value="${themePath}/images/controls/thumbs-down.png"/>"
				title="<spring:message code="music.playlist.current-song.vote.hate" />" /></a>
		</span> <a class="toggle-playlist" href="javascript:void(0);"
			title="<spring:message code="music.playlist.toggle" />"><img
			src="<c:url value="${themePath}/images/controls/playlist.png"/>" /></a>
	</div>


	<div class="songs">&nbsp;</div>

</div>