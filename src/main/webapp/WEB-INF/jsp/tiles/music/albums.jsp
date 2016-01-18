<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<c:if test="${!isAppend}">
	<script type="text/javascript">
	$(document).ready(function() {

        // Unbind declared event handlers
        $("div.dynamic-content").off("mouseover", "div.albums div.album");
        $("div.dynamic-content").off("mouseout", "div.albums div.album");
	    
	    
		<c:if test="${!isAppend}">
		window.scrollTo(0, 0);	
		</c:if>

		showFooterTabs("music", "${musicAlbumListType.className}");
		
		
		$("div.dynamic-content").on("mouseover", "div.albums div.album", function() {
			$(this).addClass("highlight");
		});
		
		$("div.dynamic-content").on("mouseout", "div.albums div.album", function() {
			$(this).removeClass("highlight");
		});
		
		
		$(window).scroll(function() {
			if ($("div.albums div.album").length == 0) {
				return;
			}

			appendContentsOnScroll("${musicAlbumListType.className}");
		});
		
	});
</script>
</c:if>


<div class="albums">
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

			<div class="album-control">
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


