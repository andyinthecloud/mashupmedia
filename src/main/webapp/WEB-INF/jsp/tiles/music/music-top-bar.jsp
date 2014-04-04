<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div id="media-player-script"></div>




<script type="text/javascript">
    var songPlaylistSelector = "#top-bar-music-player .songs";

    $(document).ready(function() {

	mashupMedia.loadLastAccessedPlaylist();

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
		$.get(mashupMedia.contextUrl + "app/ajax/media/encode/" + mediaItemId, function(data) {
		    setTimeout(function(){mashupMedia.playSong(mediaItemId)}, 5000);		    
		});
	});
	
	$("#current-song div.encode").on("click", "a.no-encoder-found", function(event) {
		window.location.href = "<c:url value="/app/configuration/encoding" />";
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

		$.post("<c:url value="/app/ajax/search/media-items-autocomplete" />", {
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
	    fireRelLink(this);
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
					<li><a href="<c:url value="/app/configuration/administration/account" />"><spring:message code="top-bar.my-account" /></a></li>
					<li><a href="<c:url value="/j_spring_security_logout" />"><spring:message code="top-bar.log-out" /></a></li>
					<li><a href="http://www.mashupmedia.org" target="_blank"><img title="Mashup Media" src="<c:url value="/images/mashupmedia-logo-inline.png" />" /></a></li>
					<c:if test="${isNewMashupMediaVersionAvailable}">
						<li class="update-available ui-corner-all"><a href="http://www.mashupmedia.org/download" target="_blank" title="<spring:message code="top-bar.new-update.message" />"><span class="ui-icon ui-icon-circle-arrow-s"></span></a></li>
					</c:if>
				</ul>
			</td>
		</tr>
	</table>

	<form action="address-quick-search-media-items" id="quick-search">
		<input type="hidden" name="genreId" value="${genreId}" /> <input type="hidden" name="orderBy" value="${orderBy}" /> <input type="hidden" name="isAscending" value="${isAscending}" /> <input type="hidden" name="mediaType" value="${mediaType}" /> <input type="text" name="searchWords" value="${searchWords}" /><input type="image"
			src="<c:url value="${themePath}/images/controls/search.png"/>" />
	</form>

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
				<span>Update Required</span> To play the media you will need to either update your browser to a recent version or update your <a href="http://get.adobe.com/flashplayer/" target="_blank">Flash plugin</a>.
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