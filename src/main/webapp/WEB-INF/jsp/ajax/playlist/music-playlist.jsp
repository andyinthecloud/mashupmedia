<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document)
			.ready(
					function() {

						$("table.song-playlist tbody").sortable();
						var playlistTable = $("table.song-playlist")
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
											"bInfo" : false,
											 
										});


					});
</script>

<div class="playlist-title">

	<input type="hidden" id="current-playlist-id" value="<c:out value="${playlist.id}" />" />
	<c:set var="playlistName" value="${playlist.name}" />
	<c:if test="${playlist.isDefault}">
		<c:set var="playlistName" value="${playlist.name}" />
		<spring:message var="playlistName" code="music.playlist.default.name" />
	</c:if>
	<c:out value="${playlistName}" />

	<select>
		<option value="">&nbsp;</option>
		<option id="playlist-action-new">
			<spring:message code="action.new" />
		</option>
		<option id="playlist-action-save">
			<spring:message code="action.save" />
		</option>
		<option id="playlist-action-save-as">
			<spring:message code="action.saveas" />
		</option>
		<option id="playlist-action-delete">
			<spring:message code="action.delete" />
		</option>
	</select> <input type="button" class="button" value="<spring:message code="action.ok" />" />
</div>

<table class="song-playlist">
	<thead>
		<tr>
			<th class="first"></th>
			<th class="song controls"><a href="javascript:void(0);"><span class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message code="music.playlist.song" /></a></th>
			<th class="album controls"><a href="javascript:void(0);"><span class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message code="music.playlist.album" /></a></th>
			<th class="artist controls"><a href="javascript:void(0);"><span class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message code="music.playlist.artist" /></a></th>
			<th class="length controls"><a href="javascript:void(0);"><span class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message code="music.playlist.length" /></a></th>
		</tr>

	</thead>
	<tbody>

		<c:forEach items="${playlist.playlistMediaItems}" var="playlistMediaItem">
			<c:set var="song" value="${playlistMediaItem.mediaItem}" />
			<c:set var="playingClass" value="" />
			<c:if test="${playlistMediaItem.playing }">
				<c:set var="playingClass" value="playing" />
			</c:if>

			<tr id="playlist-media-id-<c:out value="${song.id}"/>-media-format-${song.format}-album-id-${song.album.id}" class="<c:out value="${playingClass}"/>">

				<td class="controls"><span class="ui-icon ui-icon-carat-2-n-s"></span> <a class="delete" href="javascript:void(0);" title="<spring:message code="control.delete" />"><span
						class="ui-icon ui-icon-minus"></span></a> <a class="play" href="javascript:void(0);" title="<spring:message code="control.play" />"><span class="ui-icon ui-icon-play"></span></a> <input
					type="hidden" name="format" value="<c:out value="${song.format}" />" /><input type="hidden" name="album-id" value="<c:out value="${song.album.id}" />" /></td>

				<td class="text song-title"><c:out value="${song.displayTitle}" /></td>
				<td class="text album-name"><c:out value="${song.album.name}" /></td>
				<td class="text artist-name"><c:out value="${song.artist.name}" /></td>
				<td class="text track-length"><c:out value="${song.displayTrackLength}" /></td>

			</tr>



		</c:forEach>

	</tbody>

</table>

