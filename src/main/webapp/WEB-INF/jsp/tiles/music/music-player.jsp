<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div id="media-player-script"></div>




<script type="text/javascript">
	$(document).ready(function() {

		mashupMedia.loadPlaylist();

		$("#menu-random-albums").click(function() {
			$.address.value(addressRandomAlbums);
//			loadRandomAlbums(false);
		});

		$("#menu-albums").click(function() {
			$.address.value(addressListAlbums);
//			loadAlbums();
		});

		$("#menu-artists").click(function() {
			$.address.value(addressListArtists);
//			loadArtists();
		});

		
		
		$("#current-song .toggle-playlist").click(function() {
			var songPlaylist = $("#top-bar-music-player .songs");
			
			$(songPlaylist).toggle('slow', function() {
				var imagePath = "<c:url value="${themePath}/images/controls/open.png" />";
				if ($(songPlaylist).is(":visible")) {
					imagePath = "<c:url value="${themePath}/images/controls/close.png" />";
				} 
				$("#current-song .toggle-playlist img").attr("src", imagePath);
			});
			
			
			
			
		});
		
		$("div.sub-panel").click(function() {
			$("#top-bar-music-player .songs").slideUp("slow");			
		});
		
		$("#current-song .vote .like").click(function() {
			var mediaItemId = $("#current-song-id").val();
			$.post(mashupMedia.contextUrl + "app/ajax/vote/like", {
				"mediaItemId" : mediaItemId
			}, function(data) {
			});				
						
		});
		
		$("#current-song .vote .dislike").click(function() {
			var mediaItemId = $("#current-song-id").val();
			$.post(mashupMedia.contextUrl + "app/ajax/vote/dislike", {
				"mediaItemId" : mediaItemId
			}, function(data) {
			});				
		});
		
		
	});
</script>


<div id="top-bar-music-player" class="top-bar">

	<ul class="main-menu group">
		<li><a href="javascript:;" id="menu-random-albums"><spring:message code="top-bar.random-albums" /></a></li>
		<li><a href="javascript:;" id="menu-artists"><spring:message code="top-bar.artists" /></a></li>
		<li><a href="javascript:;" id="menu-albums"><spring:message code="top-bar.albums" /></a></li>
		<li><a href="javascript:;" id="menu-playlists"><spring:message code="top-bar.playlists" /></a></li>		
		<li><a href="javascript:;">Sunny</a></li>
		<li><a href="javascript:;"><spring:message code="top-bar.my-account" /></a></li>
		<li><a href="javascript:;"><spring:message code="top-bar.log-out" /></a></li>
	</ul>
	
	<div class="top-home-link"><a href="<c:url value="/" />"><spring:message code="top-bar.home" /></a></div>
	

	<div class="clear"></div>

	<input type="hidden" id="playlist-id" /> <input type="hidden" id="playlist-isDefault" />

	<div id="jquery_jplayer_1" class="jp-jplayer"></div>
	<div id="jp_container_1" class="jp-audio">
		<div class="jp-type-playlist">
			<div class="jp-gui jp-interface">
				<ul class="jp-controls">
					<li><a href="javascript:;" class="jp-previous" tabindex="1">previous</a></li>
					<li><a href="javascript:;" class="jp-play" tabindex="1">play</a></li>
					<li><a href="javascript:;" class="jp-pause" tabindex="1">pause</a></li>
					<li><a href="javascript:;" class="jp-next" tabindex="1">next</a></li>
					<li><a href="javascript:;" class="jp-stop" tabindex="1">stop</a></li>
					<li><a href="javascript:;" class="jp-mute" tabindex="1" title="mute">mute</a></li>
					<li><a href="javascript:;" class="jp-unmute" tabindex="1" title="unmute">unmute</a></li>
					<li><a href="javascript:;" class="jp-volume-max" tabindex="1" title="max volume">max volume</a></li>
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
						<li><a href="javascript:;" class="jp-repeat-off" tabindex="1" title="repeat off">repeat off</a></li>						
					</ul>
				</div>
			</div>
			<div class="jp-title"></div>
			<div class="jp-no-solution">
				<span>Update Required</span> To play the media you will need to either update your browser to a recent version or update your <a href="http://get.adobe.com/flashplayer/" target="_blank">Flash
					plugin</a>.
			</div>
		</div>
	</div>

	<div id="current-song">
		<input type="hidden" id="current-song-id" value="" /> 
		<table>
			<tbody>
				<tr>
					<td class="album-art"><a href="javascript:;"><img src="<c:url value="/images/no-album-art.png" />" /></a></td>
					<td class="song-title"><div class="artist-name"></div><div class="title"><spring:message code="music.playlist.current-song.empty" /></div></td>
					<td><span class="vote"> <a class="like" href="javascript:;" title="<spring:message code="music.playlist.current-song.vote.love" />"><img
				src="<c:url value="${themePath}/images/controls/thumbs-up.png"/>" /></a> <a class="dislike" href="javascript:void(0);"><img src="<c:url value="${themePath}/images/controls/thumbs-down.png"/>"
				title="<spring:message code="music.playlist.current-song.vote.hate" />" /></a>
		</span></td>
				</tr>
			</tbody>
		</table>
		<div class="toggle-playlist"><a href="javascript:;"><img src="<c:url value="${themePath}/images/controls/open.png"/>" /></a></div>
	</div>


	<div class="songs">&nbsp;</div>

</div>