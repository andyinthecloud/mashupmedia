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



					});
	

	function loadPlaylist() {
		$.post("<c:url value="/app/ajax/playlist/current-user-playlist" />",
				function(data) {
					$("#top-bar-music-player .songs").html(data);
					loadCurrentSong();
				});

	}

	function loadCurrentSong() {
		var playingRow = $("#top-bar-music-player .songs table tbody tr.playing");
		
		if ($(playingRow).length == 0) {
			return;
		}
		
		var songTitle = $(playingRow).find("td.song-title").text();
		
		var currentTrackDisplay = songTitle;
		$("#current-song .song-title").text(currentTrackDisplay);	
		$("#current-song .vote").show();
		
		var mediaId = $(playingRow).attr("id").replace("playlist-media-id-", "");
		playSong(mediaId);

	}
	
	function playSong(mediaId) {
		var songUrl = "<c:url value="/app/streaming/media/" />" + mediaId;
//		alert(songUrl);
//		$("#jquery_jplayer_1").jPlayer("destroy");
		
		/*
		$("#jquery_jplayer_1").jPlayer( "setMedia", {
			mp3: "/mashupmedia/app/streaming/media/44"
		});
		*/
		
		$("#jquery_jplayer_1").jPlayer({
			ready: function (event) {
				$(this).jPlayer("setMedia", {
//					mp3: "http://localhost:8080/mashupmedia/app/streaming/media/44/song.mp3",
					mp3: songUrl
					/*
					m4a:"http://www.jplayer.org/audio/m4a/TSP-01-Cro_magnon_man.m4a",
					oga:"http://www.jplayer.org/audio/ogg/TSP-01-Cro_magnon_man.ogg"
					*/
				});
			},
			swfPath: "http://www.jplayer.org/latest/js/Jplayer.swf",
			supplied: "mp3",
			solution: "html, flash",
			wmode: "window",
			errorAlerts: true
		});		
		
	
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
			$("#top-bar-music-player .songs").html(data);
			loadCurrentSong();
		});

	}
</script>

<form:form commandName="musicPage">


	<div class="sub-panel music-sub-panel">


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
