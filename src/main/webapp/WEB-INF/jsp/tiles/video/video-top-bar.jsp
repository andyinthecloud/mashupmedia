<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div id="media-player-script"></div>




<script type="text/javascript">
	var songPlaylistSelector = "#top-bar-music-player .songs";

	$(document).ready(function() {

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

	});
</script>


<div id="top-bar-video-player" class="top-bar">

	<table class="top-bar-menu">
		<tr>
			<td class="top-home-link"><a href="<c:url value="/" />"><spring:message code="top-bar.home" /></a></td>
			<td>
				<ul class="main-menu group">
					<li><a href="<c:url value="/app/videos" />"><spring:message code="top-bar.list-videos" /></a></li>
					<li><a href="<c:url value="/app/encode/processes" />"><spring:message code="top-bar.encoding.status" /></a></li>
					<li><a href="<c:url value="/app/configuration/administration/account" />"><spring:message code="top-bar.my-account" /></a></li>
					<li><a href="<c:url value="/j_spring_security_logout" />"><spring:message code="top-bar.log-out" /></a></li>
					<li><a href="http://www.mashupmedia.org" target="_blank"><img title="Mashup Media"
							src="<c:url value="/images/mashupmedia-logo-inline.png" />" /></a></li>
					<c:if test="${isNewMashupMediaVersionAvailable}">
						<li class="update-available ui-corner-all"><a href="http://www.mashupmedia.org/download" target="_blank"
							title="<spring:message code="top-bar.new-update.message" />"><span class="ui-icon ui-icon-circle-arrow-s"></span></a></li>
					</c:if>
				</ul>
			</td>
		</tr>
	</table>

	<form action="address-quick-search-media-items" id="quick-search">
		<input type="hidden" name="genreId" value="${genreId}" /> <input type="hidden" name="orderBy" value="${orderBy}" /> <input type="hidden"
			name="isAscending" value="${isAscending}" /> <input type="hidden" name="mediaType" value="${mediaType}" /> <input type="text" name="searchWords"
			value="${searchWords}" /><input type="image" src="<c:url value="${themePath}/images/controls/search.png"/>" />
	</form>

</div>