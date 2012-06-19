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

						loadPlaylist();

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

	function loadPlaylist() {
		$.post("<c:url value="/app/ajax/playlist/current-user-playlist" />",
				function(data) {
					$("#playlist .songs").html(data);
					showHidePlaylist();
				});

	}

	function showHidePlaylist() {
		var totalPlaylistRows = $("#playlist .songs table tbody tr").length;

		if (totalPlaylistRows < 1) {
			$("#playlist").hide();
			return;
		}

		var tdSelector = "#playlist .songs table tbody tr:eq(0) td";

		if ($(tdSelector).text() == "empty-playlist") {
			$("#playlist").hide();
		} else {
			$("#playlist").show();
		}

	}

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
			showHidePlaylist();
		});

	}
</script>

<form:form commandName="musicPage">


	


	<div class="sub-panel">




		<ul class="control-menu main-control-menu">
			<li class="first"><a id="category-menu-home" href="javascript:void(0);"><spring:message
						code="music.menu.random-albums" /></a></li>
			<li><a id="category-menu-albums" href="javascript:void(0);"><spring:message
						code="music.menu.albums" /></a></li>
			<li><a id="category-menu-artists" href="javascript:void(0);"><spring:message
						code="music.menu.artists" /></a></li>
			<li><a id="category-menu-playlists" href="javascript:void(0);"><spring:message
						code="music.menu.playlists" /></a></li>
		</ul>


		<div class="content">
			<tiles:insertAttribute name="body" />
		</div>

		<div style="clear: both;">&nbsp;</div>


	</div>
</form:form>
