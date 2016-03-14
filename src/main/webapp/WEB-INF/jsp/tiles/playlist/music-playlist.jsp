<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    var playlistSelectName = $("#playlist input[name=playlistSelectName]").val();

    $(document).ready(function() {

        // Unbind declared event handlers
        $("div.dynamic-content").off("click", "#playlist ul.playlist-items li a.play");
        $("div.dynamic-content").off("sortstop", "ul.playlist-items");
        $("div.dynamic-content").off("click", "#playlist ul.playlist-items a.delete");
        $("div.dynamic-content").off("change", "#playlist-actions");

        window.scrollTo(0, 0);
        showFooterTabs("music", "music-playlist");
        
        $("ul.playlist-items").sortable();

        <c:if test="${canSavePlaylist}">
        $("h1.edit").editable("<c:url value="/app/restful/playlist/music/save-playlist-name" />", {
            tooltip: "<spring:message code="action.click.edit" />"
        });
        </c:if>

        $("div.dynamic-content").on("sortstop", "ul.playlist-items", function(event, ui) {
            var playlistId = $("#playlist input[name=playlistId]").val();
            var mediaItemIds = [];
            savePlaylist();
        });

        $("div.dynamic-content").on("click", "#playlist ul.playlist-items a.delete", function() {
            var songRow = $(this).closest("li");
            $(songRow).remove();
            savePlaylist();
        });

        $("div.dynamic-content").on("change", "#playlist-actions", function() {
            var action = $(this).val();

            if (action == "clear") {
                $("#playlist ul.playlist-items li").remove();
                savePlaylist();
            } else if (action == "new") {
                $("#playlist ul.playlist-items li").remove();
                newPlaylist();
            } else if (action == "copy") {
                copyPlaylist()
            } else if (action == "delete") {
                deletePlaylist();
            } else if (action == "playlists") {                                
                var url = "<c:url value="/app/playlist/list/music" />";
                loadInternalPage("<spring:message code="playlists.title" />", url);                
            }
            
            return false;

        });

        $("div.dynamic-content").on("click", "#playlist ul.playlist-items a.delete", function() {
            var songRow = $(this).closest("li");
            $(songRow).remove();
            savePlaylist();
        });

        $("div.dynamic-content").on("click", "#playlist ul.playlist-items li a.play", function() {

            $("#playlist ul.playlist-items li").removeClass("playing");
            
            var songRow = $(this).closest("li");
            $(songRow).addClass("playing");

            var playlistId = $("#playlist input[name=playlistId]").val();
            var mediaItemId = parseId($(songRow).attr("id"), "media-item-id");

            $.post("<c:url value="/app/restful/playlist/music/play" />", {
                playlist: playlistId,
                mediaItem: mediaItemId
            }, function(data) {
                mashupMedia.streamSong(data);
                mashupMedia.playSong();
            });
        });

    });

    function newPlaylist() {
        var mediaItemIds = getMediaItemIds();
        $.post("<c:url value="/app/restful/playlist/music/new-playlist" />", {
            mediaItemIds: mediaItemIds
        }, function(data) {
            var url = "<c:url value="/app/playlist/music" />?playlist=" + data;
            loadInternalPage("<spring:message code="music.playlist.title" />", url)
        });
    }

    function copyPlaylist() {
        var name = $("#playlist h1").text() + " <spring:message code="playlist.copy.name.suffix" />";
        var mediaItemIds = getMediaItemIds();
        $.post("<c:url value="/app/restful/playlist/music/new-playlist" />", {
            name: name,
            mediaItemIds: mediaItemIds
        }, function(data) {
            var url = "<c:url value="/app/playlist/music" />?playlist=" + data;
            loadInternalPage("<spring:message code="music.playlist.title" />", url)
        });
    }

    function deletePlaylist() {
        var playlistId = $("#playlist input[name=playlistId]").val();
        $.post("<c:url value="/app/restful/playlist/music/delete-playlist" />", {
            playlistId: playlistId
        }, function(data) {
            var url = "<c:url value="/app/playlist/list/music" />";
            loadInternalPage("<spring:message code="playlists.title" />", url)
        });
    }

    function savePlaylist() {
        var playlistId = $("#playlist input[name=playlistId]").val();
        var mediaItemIds = getMediaItemIds();

        $.post("<c:url value="/app/restful/playlist/music/save-playlist" />", {
            playlistId: playlistId,
            mediaItemIds: mediaItemIds
        });

    }

    function getMediaItemIds() {
        var mediaItemIds = [];

        $("#playlist ul.playlist-items li").each(function(index) {
            var mediaItemId = parseId($(this).attr("id"), "media-item-id");
            mediaItemIds.push(mediaItemId);
        });

        return mediaItemIds;
    }
</script>


<div id="playlist">

	<input type="hidden" name="playlistAction" value="save" /> <input
		type="hidden" name="playlistSelectName"
		value="<spring:message code="playlist.select.name" />" /> <input
		type="hidden" name="playlistId"
		value="<c:out value="${playlist.id}" />" />



	<h1 class="edit" id="playlist-id-${playlist.id}">${playlist.name}</h1>

	<div class="hide change-name">
		<input type="text" name="playlistName" value="${playlist.name}" />
	</div>



	<div class="controls clear">

		<select id="playlist-actions" data-native-menu="false">
			<option value="">
				<spring:message code="music.playlist.actions" />
			</option>



			<c:if test="${canSavePlaylist}">
				<option value="clear">
					<spring:message code="music.playlist.action.clear" />
				</option>
			</c:if>

			<option value="new">
				<spring:message code="music.playlist.action.new" />
			</option>


			<option value="copy">
				<spring:message code="music.playlist.action.copy-to" />
			</option>

			<c:if test="${!playlist.userDefault}">
				<option value="delete">
					<spring:message code="music.playlist.action.delete" />
				</option>
			</c:if>

			<option value="playlists">
				<spring:message code="music.playlist.action.playlists" />
			</option>

		</select>

	</div>

	<ul class="playlist-items">

		<c:forEach items="${playlist.accessiblePlaylistMediaItems}"
			var="playlistMediaItem">
			<c:set var="song" value="${playlistMediaItem.mediaItem}" />
			<c:set var="playingClass" value="" />
			<c:if test="${playlistMediaItem.playing }">
				<c:set var="playingClass" value="playing" />
			</c:if>

			<li id="media-item-id-<c:out value="${song.id}"/>"
				class="<c:out value="${playingClass}"/>"><a href="javascript:;"
				class="play"> <img
					src="<c:url value="${themePath}/images/controls/play.png"/>" />
			</a>

				<div class="item">
					<div class="title">${song.displayTitle}</div>
					<div class="meta">${song.artist.name}-${song.album.name}</div>
				</div>

				<div class="icons-right">
					<img class="cursor-move"
						alt="<spring:message code="action.reorder"/>"
						title="<spring:message code="action.reorder"/>"
						src="<c:url value="${themePath}/images/controls/up-down.png"/>" />

					<a href="javascript:;" class="delete"><img
						alt="<spring:message code="action.playlist.item.remove"/>"
						title="<spring:message code="action.playlist.item.remove"/>"
						src="<c:url value="${themePath}/images/controls/delete.png"/>" /></a>
				</div></li>

		</c:forEach>

	</ul>
</div>
