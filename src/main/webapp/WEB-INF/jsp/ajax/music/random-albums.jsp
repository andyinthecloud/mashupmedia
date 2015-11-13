<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<c:if test="${!isAppend}">
	<script type="text/javascript">
	$(document).ready(function() {

		<c:if test="${!isAppend}">
		window.scrollTo(0, 0);
		</c:if>

		$("div.music-sub-panel").on("mouseover", "div.albums div.album", function() {
			$(this).addClass("highlight");
		});
		
		$("div.music-sub-panel").on("mouseout", "div.albums div.album", function() {
			$(this).removeClass("highlight");
		});

		
		$("div.music-sub-panel").on("click", "div.albums div.album a", function() {
			fireRelLink(this);
		});

		$(window).scroll(function() {
			if ($("div.random-album-art div.album").length == 0) {
				return;
			}

			appendContentsOnScroll();
		});
		



	});
</script>
</c:if>


<div class="random-album-art albums">
	<c:forEach items="${albums}" var="album">
		<div class="album" id="album-id-${album.id}">
			<a href="javascript:;" rel="address:/address-load-album-${album.id}">
				<img
				src="<c:url value="/app/music/album-art/thumbnail/${album.id}" />"
				title="${album.artist.name} - ${album.name}"
				alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
			</a>


			<div class="album-title">
				<div class="artist-name">
					<a href="javascript:;"
						rel="address:/address-artist-${album.artist.id}">${album.artist.name}</a>
				</div>
				<div class="album-name">
					<a href="javascript:;"
						rel="address:/address-load-album-${album.id}">${album.name}</a>
				</div>
			</div>


			<div class="mobile-album-control">
				<a href="#popupMenu" data-rel="popup" data-transition="slideup"
					class="ui-btn ui-shadow ui-corner-all ui-icon-action ui-btn-icon-notext">Album
					actions</a>
				<div data-role="popup" id="popupMenu" data-theme="b">
					<ul data-role="listview" data-inset="true"
						style="min-width: 210px;">
						<li><a class="play" href="javascript:;"><spring:message code="action.play" /></a></li>
						<c:if test="${isPlaylistOwner}">
							<li><a class="add" href="javascript:;"><spring:message code="action.add" /></a></li>
						</c:if>
					</ul>
				</div>
			</div>

			<div class="desktop-album-control">
				<a class="play" href="javascript:;"
					title="<spring:message code="action.play" />"><img
					alt="<spring:message code="action.play"/>"
					title="<spring:message code="action.play"/>"
					src="<c:url value="${themePath}/images/controls/play.png"/>" /></a>
				<c:if test="${isPlaylistOwner}">
					<a class="add" href="javascript:;"
						title="<spring:message code="action.add" />"><img
						alt="<spring:message code="action.add"/>"
						title="<spring:message code="action.add"/>"
						src="<c:url value="${themePath}/images/controls/add.png"/>" /></a>
				</c:if>
			</div>
		</div>
	</c:forEach>
</div>


