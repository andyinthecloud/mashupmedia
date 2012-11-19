<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		$("table.song-playlist tbody").sortable({
			stop: function(event, ui) {
		    }
		});
		var playlistTable = $("table.song-playlist").dataTable({
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
		
		$("table.song-playlist td.controls a.delete").on("click", function() {
			var songRow = $(this).closest("tr");
			if ($(songRow).hasClass(mashupMedia.playingClass)) {
				mashupMedia.destroyPlayer();
			}			
			$(this).closest("tr").remove();
		});

		$("table.song-playlist td.controls a.play").on("click", function() {
			$("table.song-playlist tbody tr").removeClass(mashupMedia.playingClass);
			var songRow = $(this).closest("tr");
			$(songRow).addClass(mashupMedia.playingClass);
			mashupMedia.loadSong(true);
		});
		
		$("#playlist-actions").change(function() {
			var action = $(this).val();
			if (action == "") {
				return;
			}
			
			if (action == "clear") {
				mashupMedia.clearPlaylist();
			}
			
			$(this).val("");
			
		});
		
		$("#save-current-playlist").click(function() {
			mashupMedia.saveCurrentPlaylist();
		});
		

	});
</script>

<div class="playlist-title">

	<input type="hidden" id="current-playlist-id" value="<c:out value="${playlist.id}" />" />
	
	
	
	<label><c:out value="${playlist.name}" /></label>
		

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
 
		<option id="save-as">
			<spring:message code="action.saveas" />
		</option>
 
		<c:if test="${!playlist.isUserDefault}">
			<option id="delete">
				<spring:message code="action.delete" />
			</option>		
		</c:if>

	</select> 
		
	<c:if test="${playlist.isUserDefault}">
		<input type="button" class="button" id="save-current-playlist" value="<spring:message code="action.save" />" />
	</c:if>
	
</div>

<table class="song-playlist">
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
						class="ui-icon ui-icon-minus"></span></a> <a class="play" href="javascript:;" title="<spring:message code="action.play" />"><span class="ui-icon ui-icon-play"></span></a> <input
					type="hidden" name="format" value="<c:out value="${song.format}" />" /><input type="hidden" name="album-id" value="<c:out value="${song.album.id}" />" /></td>

				<td class="text song-title"><c:out value="${song.displayTitle}" /></td>
				<td class="text album-name"><c:out value="${song.album.name}" /></td>
				<td class="text artist-name"><c:out value="${song.artist.name}" /></td>
				<td class="text track-length"><c:out value="${song.displayTrackLength}" /></td>

			</tr>



		</c:forEach>

	</tbody>

</table>

