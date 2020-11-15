<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div id="media-player-script"></div>




<script type="text/javascript">
    var songPlaylistSelector = "#top-bar-music-player .songs";

    $(document).ready(function() {

	mashupMedia.loadLastAccessedPlaylist();
	
	
	$("#jp_container_1 .jp-play").click(function() {
		//$(mashupMedia.jPlayerId).jPlayer.pause;
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

	$("#current-song div.encode").on("click", "a.encode-file", function(event) {
		var mediaItemId = $("#current-song-id").val();
		$.get(mashupMedia.contextUrl + "app/ajax/media/encode/" + mediaItemId + "/mp3", function(data) {
			$("#current-song td.song-title div.encode").html("<spring:message code="music.playlist.encode.queued" />");
		});
	});
	
	$("#current-song div.encode").on("click", "a.no-encoder-found", function(event) {
		window.location.href = "<c:url value="/configuration/encoding" />";
	});

	var defaultSearchText = "<spring:message code="search" />";
	var searchField = $("#quick-search input[type=text]");
	var searchText = getTextFromField(searchField);
	if (searchText == "") {
	    $(searchField).val(defaultSearchText);
	}

	$(searchField).focus(function() {
	    var text = getTextFromField(searchField);
	    if (text == defaultSearchText) {
		$(searchField).val("");
	    }
	});

	$(searchField).blur(function() {
	    var text = getTextFromField(searchField);
	    if (text == "") {
		$(searchField).val(defaultSearchText);
	    }
	});

	$(searchField).autocomplete({
	    source : function(request, response) {
		var searchText = getTextFromField(searchField);
		//Ignore typed spaces
		if (endsWith(searchText, " ")) {
		    return;
		}

		$.post("<c:url value="/ajax/search/media-items-autocomplete" />", {
		    "searchWords" : searchText
		}, function(data) {
		    response(jQuery.map(data, function(item) {
			return {
			    label : item.suggestion
			}
		    }));
		});

	    },
	    minLength : 2,
	    select : function(event, ui) {
		$("#top-bar-music-player div.search-box input[type=text]").val(ui.item.suggestion);
	    }
	});


	$("#current-song").on("click", "a", function() {
	    
	});

		
	
    });
    
    function getAlbumId() {
    	var albumId = $("#current-song td.album-art img").attr("src");
    	albumId = albumId.replace(/.*album-art-thumbnail\//, "");
    	return albumId;
    }
    
</script>


<div id="top-bar-music-player" class="top-bar">

	<table class="top-bar-menu">
		<tr>
			<td class="top-home-link"><a href="<c:url value="/" />"><spring:message code="top-bar.home" /></a></td>
			<td>
				<ul class="main-menu group">
					<li><a href="javascript:;" rel="address:/address-random-albums"><spring:message code="top-bar.random-albums" /></a></li>
					<li><a href="javascript:;" rel="address:/address-list-artists"><spring:message code="top-bar.artists" /></a></li>
					<li><a href="javascript:;" rel="address:/address-list-albums"><spring:message code="top-bar.albums" /></a></li>
					<li><a href="javascript:;" rel="address:/address-list-playlists"><spring:message code="top-bar.playlists" /></a></li>
					<sec:authorize access="hasRole('ROLE_ADMINISTRATOR')">
					<li><a href="<c:url value="/encode/processes" />"><spring:message code="top-bar.encoding.queue" /></a></li>
					</sec:authorize>
					<li><a href="<c:url value="/configuration/administration/account" />"><spring:message code="top-bar.my-account" /></a></li>
					<li><a id="log-out" href="#"><spring:message code="top-bar.log-out" /></a></li>
					<li><a href="http://www.mashupmedia.org" target="_blank"><img title="Mashup Media" src="<c:url value="/images/mashupmedia-logo-inline.png" />" /></a></li>
					<c:if test="${isNewMashupMediaVersionAvailable}">
						<li class="update-available ui-corner-all"><a href="http://www.mashupmedia.org/download" target="_blank" title="<spring:message code="top-bar.new-update.message" />"><span class="ui-icon ui-icon-circle-arrow-s"></span></a></li>
					</c:if>
				</ul>
			</td>
		</tr>
	</table>

	<form action="address-quick-search-media-items" id="quick-search" class="quick-search">
		<input type="hidden" name="orderBy" value="${orderBy}" /> <input type="hidden" name="isAscending" value="${isAscending}" /> <input type="hidden" name="mediaType" value="${mediaType}" /> <input type="text" name="searchWords" value="${searchWords}" /><input type="image"
			src="<c:url value="${themePath}/images/controls/search.png"/>" />
	</form>

	<div class="clear"></div>
	
	<div id="jquery_jplayer_1" class="jp-jplayer"></div>
	<div id="jp_container_1" class="jp-audio" role="application" aria-label="media player">
		<div class="jp-type-playlist">
			<div class="jp-gui jp-interface">
				<div class="jp-volume-controls">
					<button class="jp-mute" role="button" tabindex="0">mute</button>
					<button class="jp-volume-max" role="button" tabindex="0">max volume</button>
					<div class="jp-volume-bar">
						<div class="jp-volume-bar-value"></div>
					</div>
				</div>
				<div class="jp-controls-holder">
					<div class="jp-controls">
						<button class="jp-previous" role="button" tabindex="0">previous</button>
						<button class="jp-play" role="button" tabindex="0">play</button>
						<button class="jp-stop" role="button" tabindex="0">stop</button>
						<button class="jp-next" role="button" tabindex="0">next</button>
					</div>
					<div class="jp-progress">
						<div class="jp-seek-bar">
							<div class="jp-play-bar"></div>
						</div>
					</div>
					<div class="jp-current-time" role="timer" aria-label="time">&nbsp;</div>
					<div class="jp-duration" role="timer" aria-label="duration">&nbsp;</div>
					<div class="jp-toggles">
						<button class="jp-repeat" role="button" tabindex="0">repeat</button>
						<button class="jp-shuffle" role="button" tabindex="0">shuffle</button>
					</div>
				</div>
			</div>
			<div class="jp-playlist">
				<ul>
					<li>&nbsp;</li>
				</ul>
			</div>
			<div class="jp-no-solution">
				<span>Update Required</span>
				To play the media you will need to either update your browser to a recent version or update your <a href="http://get.adobe.com/flashplayer/" target="_blank">Flash plugin</a>.
			</div>
		</div>
	</div>	
	

	<div id="current-song">
		<input type="hidden" id="current-song-id" value="" />
		<table>
			<tbody>
				<tr>
					<td class="album-art"><a href="javascript:;"><img src="<c:url value="/images/no-album-art.png" />" /></a></td>
					<td class="song-title"><div class="artist-name"></div>
						<div class="title">
							<spring:message code="music.playlist.current-song.empty" />
						</div>
						<div class="playlist">
							<input type="hidden" id="current-playlist-id" value="" /> <a href="javascript:;"></a>
						</div>
						<div class="encode">&nbsp;</div></td>
					<td class="vote"><span class="vote"> <a class="like" href="javascript:;" title="<spring:message code="music.playlist.current-song.vote.love" />"><img src="<c:url value="${themePath}/images/controls/thumbs-up.png"/>" /></a> <a class="dislike" href="javascript:;"><img
								src="<c:url value="${themePath}/images/controls/thumbs-down.png"/>" title="<spring:message code="music.playlist.current-song.vote.hate" />" /></a>
					</span></td>
				</tr>
			</tbody>
		</table>
	</div>

</div>