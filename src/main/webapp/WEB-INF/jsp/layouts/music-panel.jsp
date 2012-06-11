<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document)
			.ready(
					function() {

						$.address.change(function(event) {
							var address = event.value;
							address = address.replace("/", "");

							switch (address) {
							case "category-menu-home":
								loadRandomAlbums();
								break;
							case "category-menu-albums":
								loadAlbums();
								break;
							case "category-menu-artists":
								loadArtists();
								break;
							default:
								loadRandomAlbums();
								break;
							}
						});

						loadRandomAlbums();

						$("#category-menu-home").click(function() {
							$.address.value($(this).attr("id"));
							loadRandomAlbums();
						});

						$("#category-menu-albums").click(function() {
							$.address.value($(this).attr("id"));
							loadAlbums();
						});

						$("#category-menu-artists").click(function() {
							$.address.value($(this).attr("id"));
							loadArtists();
						});

						$("#jquery_jplayer_1")
								.jPlayer(
										{
											ready : function(event) {
												$(this)
														.jPlayer(
																"setMedia",
																{
																	m4a : "http://www.jplayer.org/audio/m4a/TSP-01-Cro_magnon_man.m4a",
																	oga : "http://www.jplayer.org/audio/ogg/TSP-01-Cro_magnon_man.ogg"
																});
											},
											swfPath : "<c:url value="/jquery-plugins/jquery.jplayer/2.1.0" />",
											supplied : "m4a, oga",
											wmode : "window"
										});

					});

	function loadRandomAlbums() {
		$.get("<c:url value="/app/ajax/music/random-albums" />",
				function(data) {
					$("div.panel div.content").html(data);
				});
	}

	function loadAlbums() {
		$.get("<c:url value="/app/ajax/music/albums" />", function(data) {
			$("div.panel div.content").html(data);
		});
	}

	function loadArtists() {
		$.get("<c:url value="/app/ajax/music/artists" />", function(data) {
			$("div.panel div.content").html(data);
		});
	}

	function playAlbum(albumId) {
		$.post("<c:url value="/app/ajax/playlist/play-album" />", {
			albumId : albumId
		}, function(data) {
			$("#playlist .songs").html(data);
		});

	}
</script>

<form:form commandName="musicPage">

	<div class="sub-panel">

		<div id="playlist" class="sub-rounded-corners">
			<form:hidden path="playlist.id" />
			<form:hidden path="playlist.default" />
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
						<ul>
							<li>Bubble</li>
						</ul>
					</div>
					<div class="jp-no-solution">
						<span>Update Required</span> To play the media you will need to either update your browser to
						a recent version or update your <a href="http://get.adobe.com/flashplayer/" target="_blank">Flash
							plugin</a>.
					</div>
				</div>
			</div>
			<div class="songs"></div>
		</div>


		<ul class="control-menu main-control-menu">
			<li class="first"><a id="category-menu-home" href="javascript:void(0);"><spring:message
						code="music.menu.random-albums" /></a></li>
			<li><a id="category-menu-albums" href="javascript:void(0);"><spring:message
						code="music.menu.albums" /></a></li>
			<li><a id="category-menu-artists" href="javascript:void(0);"><spring:message
						code="music.menu.artists" /></a></li>
		</ul>


		<div class="content">
			<tiles:insertAttribute name="body" />
		</div>

		<div style="clear: both;">&nbsp;</div>


	</div>
</form:form>
