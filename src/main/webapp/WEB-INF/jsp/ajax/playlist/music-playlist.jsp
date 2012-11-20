<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document)
			.ready(
					function() {
						$("#playlist table.songs tbody").sortable({
							stop : function(event, ui) {
							}
						});
						var playlistTable = $("#playlist table.songs")
								.dataTable(
										{
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

						$("#playlist table.songs td.controls a.delete").click(
								function() {
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
									mashupMedia.loadSong(mediaItemId, true);
								});

						$("#playlist-actions").change(function() {
							var action = $(this).val();
							if (action == "") {
								return;
							}

							var playlistAction = "save";
							if (action == "clear") {
								mashupMedia.clearPlayer();
								$("#playlist table.songs tbody tr").remove();
							} else if (action == "change-name") {
								$("#playlist h1").hide();
								$("#playlist div.change-name").show();
								playlistAction = "save";
							} else if (action == "new") {
								$("#playlist h1").hide();
								$("#playlist div.change-name").show();
								$("#playlist table.songs tbody tr").remove();
								playlistAction = "new";
							} else if (action == "save-as") {
								$("#playlist h1").hide();
								$("#playlist div.change-name").show();
								playlistAction = "save-as";
							} else if (action == "delete") {
								playlistAction = "delete";
							}

							$("#playlist input[name=playlistAction]").val(playlistAction);
							$(this).val("");

						});

						$("#save-current-playlist").click(function() {
							savePlaylist();
						});

					});

	function savePlaylist() {
		var playlistId = $("#current-playlist-id").val();
		var mediaItemIds = new Array();
		var playlistAction = $("#playlist input[type=playlistAction]").val();
		
		$("#playlist table.songs tbody tr").each(function(index) {
			var rowId = $(this).attr("id");
			var mediaItemId = parseId(rowId, "playlist-media-id");
			mediaItemIds[index] = mediaItemId;
		});
		var playlistName = $("#playlist input[name=playlistName]").val();

		$.post(mashupMedia.contextUrl + "app/ajax/playlist/" + playlistAction, {
			"playlistId" : playlistId,
			"playlistName" : playlistName,
			"mediaItemIds" : mediaItemIds
		}, function(data) {
			$("#playlist input[type=playlistAction]").val("save");

		});

	};
</script>


<div id="playlist">
	
	<input type="hidden" name="playlistAction" value="save"/>
	
	<h1>
		${playlist.name}
		<c:if test="${playlist.isUserDefault}">
			<spring:message code="playlist.user.default" />
		</c:if>

	</h1>

	<div class="hide change-name">
		<input type="text" name="playlistName" value="${playlist.name}" />
	</div>


	<div class="controls">

		<input type="hidden" id="current-playlist-id" value="<c:out value="${playlist.id}" />" /> <select id="playlist-actions">
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

			<option id="save-as">
				<spring:message code="action.saveas" />
			</option>

			<c:if test="${!playlist.isUserDefault}">
				<option id="delete">
					<spring:message code="action.delete" />
				</option>
			</c:if>

		</select>

		<input type="button" class="button" id="save-current-playlist" value="<spring:message code="action.save" />" />
		
		<input type="button" class="button play" id="play-playlist" value="<spring:message code="action.play" />" />
	</div>

	<table class="songs">
		<thead>
			<tr>
				<th class="first"></th>
				<th class="song controls"><a href="javascript:;"><span class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message code="music.playlist.song" /></a></th>
				<th class="album controls"><a href="javascript:;"><span class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message code="music.playlist.album" /></a></th>
				<th class="artist controls"><a href="javascript:;"><span class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message code="music.playlist.artist" /></a></th>
				<th class="length controls"><a href="javascript:;"><span class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message code="music.playlist.length" /></a></th>
			</tr>

		</thead>
		<tbody>

			<c:forEach items="${playlist.playlistMediaItems}" var="playlistMediaItem">
				<c:set var="song" value="${playlistMediaItem.mediaItem}" />
				<c:set var="playingClass" value="" />
				<c:if test="${playlistMediaItem.playing }">
					<c:set var="playingClass" value="playing" />
				</c:if>

				<tr id="playlist-media-id-<c:out value="${song.id}"/>-media-format-${song.mediaContentType}-album-id-${song.album.id}" class="<c:out value="${playingClass}"/>">

					<td class="controls"><span class="ui-icon ui-icon-carat-2-n-s"></span> <a class="delete" href="javascript:;" title="<spring:message code="action.playlist.delete" />"><span
							class="ui-icon ui-icon-minus"></span></a> <a class="play" href="javascript:;" title="<spring:message code="action.play" />"><span class="ui-icon ui-icon-play"></span></a> <input type="hidden"
						name="format" value="<c:out value="${song.format}" />" /><input type="hidden" name="album-id" value="<c:out value="${song.album.id}" />" /></td>

					<td class="text song-title"><c:out value="${song.displayTitle}" /></td>
					<td class="text album-name"><c:out value="${song.album.name}" /></td>
					<td class="text artist-name"><c:out value="${song.artist.name}" /></td>
					<td class="text track-length"><c:out value="${song.displayTrackLength}" /></td>

				</tr>



			</c:forEach>

		</tbody>

	</table>

</div>
