<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<c:forEach items="${playlistMediaItems}" var="playlistMediaItem">
	<c:set var="song" value="${playlistMediaItem.mediaItem}" />
	<c:set var="playingClass" value="" />
	<c:if test="${playlistMediaItem.playing }">
		<c:set var="playingClass" value="playing" />
	</c:if>
				
	<tr id="playlist-media-id-<c:out value="${song.id}"/>-media-format-${song.mediaContentType}-album-id-${song.album.id}" class="<c:out value="${playingClass}"/>">
		<td class="controls"><span class="ui-icon ui-icon-carat-2-n-s"></span> <a class="delete" href="javascript:;" title="<spring:message code="control.delete" />"><span
				class="ui-icon ui-icon-minus"></span></a> <a class="play" href="javascript:;" title="<spring:message code="control.play" />"><span class="ui-icon ui-icon-play"></span></a> <input
			type="hidden" name="format" value="<c:out value="${song.format}" />" /><input type="hidden" name="album-id" value="<c:out value="${song.album.id}" />" /></td>

		<td class="text song-title"><c:out value="${song.displayTitle}" /></td>
		<td class="text album-name"><c:out value="${song.album.name}" /></td>
		<td class="text artist-name"><c:out value="${song.artist.name}" /></td>
		<td class="text track-length"><c:out value="${song.displayTrackLength}" /></td>
	</tr>
</c:forEach>
