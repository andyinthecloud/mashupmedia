<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {

		$("table.song-playlist tbody").sortable();
		$("table.song-playlist").dataTable({
			"bFilter" : false,
			"aoColumnDefs" : [ {
				"bSortable" : false,
				"aTargets" : [ 0 ]
			} ],
			"bPaginate" : false,
			"bAutoWidth": true,
			"bInfo": false
		});

	});
</script>

<table class="song-playlist">
	<thead>
		<tr>
			<th></th>
			<th class="song controls"><a href="javascript:void(0);"><span
					class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message code="music.playlist.song" /></a></th>
			<th class="album controls"><a href="javascript:void(0);"><span
					class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message code="music.playlist.album" /></a></th>
			<th class="artist controls"><a href="javascript:void(0);"><span
					class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message code="music.playlist.artist" /></a></th>
			<th class="length controls"><a href="javascript:void(0);"><span
					class="ui-icon ui-icon-carat-2-n-s"></span> <spring:message code="music.playlist.length" /></a></th>
		</tr>

	</thead>
	<tbody>

		<c:forEach items="${playlist.playlistMediaItems}" var="playlistMediaItem">
			<c:set var="song" value="${playlistMediaItem.mediaItem}" />

			<tr id="playlist-mediaId-<c:out value="${song.id}"/>">
				<td class="controls"><span class="ui-icon ui-icon-carat-2-n-s"></span> <a class="delete"
					href="javascript:void(0);" title="<spring:message code="control.delete" />"><span
						class="ui-icon ui-icon-minus"></span></a> <a class="play" href="javascript:void(0);"
					title="<spring:message code="control.play" />"><span class="ui-icon ui-icon-play"></span></a></td>

				<td class="text"><c:out value="${song.displayTitle}" /></td>
				<td class="text"><c:out value="${song.album.name}" /></td>
				<td class="text"><c:out value="${song.artist.name}" /></td>
				<td class="text"><c:out value="${song.displayTrackLength}" /></td>

			</tr>



		</c:forEach>

	</tbody>

</table>

