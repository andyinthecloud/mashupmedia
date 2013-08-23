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
			<a href="javascript:;" rel="address:/address-load-album-${album.id}"> <img src="<c:url value="/app/music/album-art/thumbnail/${album.id}" />"
				title="${album.artist.name} - ${album.name}" alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
			</a>

			<div>
				<a href="javascript:;" rel="address:/address-artist-${album.artist.id}">${album.artist.name}</a>
			</div>
			<div>
				<a href="javascript:;" rel="address:/address-load-album-${album.id}">${album.name}</a>
			</div>

			<div class="album-control">

				<a class="play" href="javascript:;" title="<spring:message code="action.play" />"><span class="ui-icon ui-icon-play"></span></a>
				<c:if test="${isPlaylistOwner}">
					<a class="add" href="javascript:;" title="<spring:message code="action.add" />"><span class="ui-icon ui-icon-plus"></span></a>
				</c:if>

			</div>
		</div>
	</c:forEach>
</div>


