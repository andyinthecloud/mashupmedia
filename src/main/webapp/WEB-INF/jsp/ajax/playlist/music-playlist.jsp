<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>




<table>
	<tbody>
		<c:forEach items="${playlist.playlistMediaItems}"
			var="playlistMediaItem">
			<c:set var="song" value="${playlistMediaItem.mediaItem}" />
			<tr id="playlist-mediaId-<c:out value="${song.id}"/>">
				<td><c:out value="${song.displayTitle}" /></td>
				<td><c:out value="${song.album.name}" /></td>
				<td><c:out value="${song.artist.name}" /></td>
				<td><c:out value="${song.displayTrackLength}" /></td>
			</tr>

		</c:forEach>


	</tbody>

</table>

