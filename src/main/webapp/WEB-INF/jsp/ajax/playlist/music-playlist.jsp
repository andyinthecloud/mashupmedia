<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	var playlistSelectName = $("#playlist input[name=playlistSelectName]")
			.val();

	$(document).ready(function() {

		$("#playlist table.songs tbody").sortable({
			stop : function(event, ui) {
			}
		});
		
		var playlistTable = $("#playlist table.songs").dataTable({
			"bFilter" : false,
			"oLanguage" : {
				"sEmptyTable" : "<spring:message code="music.playlist.empty" />"
			},
			"aoColumnDefs" : [ {
				"bSortable" : false,
				"aTargets" : [ 0 ]
			} ],
			"bPaginate" : false,
			"bAutoWidth" : false,
			"bInfo" : false
		});
	
		$("#playlist table.songs td.controls a.delete").click(function() {
			var songRow = $(this).closest("tr");
			if ($(songRow).hasClass(
					mashupMedia.playingClass)) {
				mashupMedia.destroyPlayer();
			}
			$(this).closest("tr").remove();
		});
	
		$("#playlist table.songs td.controls a.play").click(
				function() {
					$("table.songs tbody tr").removeClass(
							mashupMedia.playingClass);
					var songRow = $(this).closest("tr");
					$(songRow).addClass(
							mashupMedia.playingClass);
					var mediaItemId = $(songRow).attr("id");
					var mediaItemId = parseId(mediaItemId,
							"playlist-media-id");
					var playlistId = $(
							"#playlist input[name=playlistId]")
							.val();
					mashupMedia.loadSongFromPlaylist(
							playlistId, mediaItemId, true);
				});
	
		$("#playlist-actions").change(
				function() {
					var action = $(this).val();
					if (action == "") {
						return;
					}
	
					var isHidePlaylistFeatures = false;
	
					var playlistName = "${playlist.name}";
					var playlistAction = "save";
					if (action == "clear") {
						mashupMedia.clearPlayer();
						$("#playlist table.songs tbody tr")
								.remove();
					} else if (action == "change-name") {
						$("#playlist h1").hide();
						$("#playlist div.change-name").show();
						playlistAction = "save";
					} else if (action == "new") {
						$("#playlist h1").hide();
						$("#playlist div.change-name").show();
						$("#playlist table.songs tbody tr")
								.remove();
						playlistName = playlistSelectName;
						isHidePlaylistFeatures = true;
						playlistAction = "new";
					} else if (action == "save-as") {
						$("#playlist h1").hide();
						$("#playlist div.change-name").show();
						playlistName = playlistSelectName;
						playlistAction = "save-as";
					} else if (action == "delete") {
						playlistAction = "delete";
					}
	
					$("#playlist input[name=playlistName]")
							.val(playlistName);
					$("#playlist input[name=playlistAction]")
							.val(playlistAction);
	
					if (isHidePlaylistFeatures) {
						$("#playlist table.songs").hide();
						$("#play-playlist").hide();
					} else {
						$("#playlist table.songs").show();
						$("#play-playlist").show();
					}
	
				});
	
		$("#save-current-playlist").click(function() {
			savePlaylist();
		});
	
		$("#play-playlist").click(
				function() {
					var playlistId = $(
							"#playlist input[name=playlistId]")
							.val();
					mashupMedia.loadPlaylist(playlistId);
					mashupMedia.playNextSong();
				});
	
		$("#playlist input[name=playlistName]").blur(
				function() {
					var playlistName = $.trim($(this).val());
					if (playlistName.length == 0) {
						$(this).val(playlistSelectName);
					}
				});
	
		$("#playlist input[name=playlistName]").focus(
				function() {
					var playlistName = $.trim($(this).val());
					if (playlistName == playlistSelectName) {
						$(this).val("");
					}
				});
	
		var playlistId = "${playlist.id}";
		if (playlistId.length == 0 || isNaN(playlistId)) {
			$("#playlist-actions").val("new")
			$("#playlist-actions").trigger("change");
		}

	});

	function savePlaylist() {

		var playlistName = $
				.trim($("#playlist input[name=playlistName]").val());
		var playlistAction = $("#playlist input[name=playlistAction]").val();
		var isNewPlaylistId = false;
		if (playlistAction == "save-as" || playlistAction == "new") {
			isNewPlaylistId = true;
		}

		if (isNewPlaylistId) {
			if (playlistName.length == 0 || playlistName == playlistSelectName) {
				alert("<spring:message code="playlist.error.empty-name" />");
				return;
			}
		}

		var playlistId = $("#playlist input[name=playlistId]").val();
		var mediaItemIds = new Array();

		$("#playlist table.songs tbody tr").each(function(index) {
			var rowId = $(this).attr("id");
			if (rowId != undefined) {
				var mediaItemId = parseId(rowId, "playlist-media-id");
				mediaItemIds[index] = mediaItemId;
			}
		});

		$.post(mashupMedia.contextUrl + "app/ajax/playlist/" + playlistAction,
				{
					"playlistId" : playlistId,
					"playlistName" : playlistName,
					"mediaItemIds" : mediaItemIds
				}, function(data) {
					/*
					$("#playlist input[type=playlistAction]").val("save");
					$("#playlist-actions").val("");
					 */
					if (playlistAction == "delete") {
						loadPlaylists();
						return;
					}

					if (isNewPlaylistId) {
						playlistId = data.response.message;
					}

					loadPlaylist(playlistId);
				});

	};
</script>


<div id="playlist">

	<input type="hidden" name="playlistAction" value="save" /> <input type="hidden" name="playlistSelectName"
		value="<spring:message code="playlist.select.name" />" /> <input type="hidden" name="playlistId" value="<c:out value="${playlist.id}" />" />

	<h1>${playlist.name}</h1>

	<div class="hide change-name">
		<input type="text" name="playlistName" value="${playlist.name}" />
	</div>


	<div class="controls">

		<select id="playlist-actions">
			<option value="">
				<spring:message code="music.playlist.actions" />
			</option>

			<option value="clear">
				<spring:message code="action.clear" />
			</option>

			<option value="new">
				<spring:message code="action.new" />
			</option>

			<option value="change-name">
				<spring:message code="action.change-name" />
			</option>

			<option value="save-as">
				<spring:message code="action.saveas" />
			</option>

			<c:if test="${!playlist.isUserDefault}">
				<option value="delete">
					<spring:message code="action.delete" />
				</option>
			</c:if>

		</select>

		<c:if test="${canSavePlaylist}">
			<input type="button" class="button" id="save-current-playlist" value="<spring:message code="action.save" />" />
		</c:if>

		<input type="button" class="button play" id="play-playlist" value="<spring:message code="action.play" />" />
	</div>

	<table class="songs">
		<thead>
			<tr>
				<th class="first"></th>
				<th class="song controls"><a href="javascript:;"><span class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message
							code="music.playlist.song" /></a></th>
				<th class="album controls"><a href="javascript:;"><span class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message
							code="music.playlist.album" /></a></th>
				<th class="artist controls"><a href="javascript:;"><span class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message
							code="music.playlist.artist" /></a></th>
				<th class="length controls"><a href="javascript:;"><span class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message
							code="music.playlist.length" /></a></th>
			</tr>

		</thead>
		<tbody>

			<c:forEach items="${playlist.accessiblePlaylistMediaItems}" var="playlistMediaItem">
				<c:set var="song" value="${playlistMediaItem.mediaItem}" />
				<c:set var="playingClass" value="" />
				<c:if test="${playlistMediaItem.playing }">
					<c:set var="playingClass" value="playing" />
				</c:if>

				<tr id="playlist-media-id-<c:out value="${song.id}"/>-media-format-${song.mediaContentType}-album-id-${song.album.id}"
					class="<c:out value="${playingClass}"/>">

					<td class="controls"><span class="ui-icon ui-icon-carat-2-n-s"></span> <a class="delete" href="javascript:;"
						title="<spring:message code="action.playlist.delete" />"><span class="ui-icon ui-icon-minus"></span></a> <a class="play" href="javascript:;"
						title="<spring:message code="action.play" />"><span class="ui-icon ui-icon-play"></span></a> <input type="hidden" name="format"
						value="<c:out value="${song.format}" />" /><input type="hidden" name="album-id" value="<c:out value="${song.album.id}" />" /></td>

					<td class="text song-title"><c:out value="${song.displayTitle}" /></td>
					<td class="text album-name"><c:out value="${song.album.name}" /></td>
					<td class="text artist-name"><c:out value="${song.artist.name}" /></td>
					<td class="text track-length"><c:out value="${song.displayTrackLength}" /></td>

				</tr>



			</c:forEach>

		</tbody>

	</table>

</div>
