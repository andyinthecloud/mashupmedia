<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>




<table>
<tbody>
<c:forEach items="playlist.playlistMediaItems" var="playlistMediaItem">
	<c:set var="song" value="" />
<tr id="playlist-mediaId-<c:out value="${ }"/>">

<td></td>

</tr>

</c:forEach>


</tbody>

</table>

