<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<script type="text/javascript">
	$(document).ready(function() {
		$("div.music-sub-panel").unbind();
		window.scrollTo(0, 0);

		$("div.music-sub-panel").on("click", "div.action-buttons a, ul.main-menu li a", function() {
			fireRelLink(this);
		});

	});
</script>

<div class="action-buttons">
	<a class="button" href="javascript:;" rel="address:/address-playlist-0"><spring:message code="playlists.create" /></a>
</div>

<h1 class="bottom-margin">
	<spring:message code="playlists.title" />
</h1>

<ul class="main-menu">
	<c:forEach items="${playlists}" var="playlist">
		<li><a href="javascript:;" rel="address:/address-playlist-${playlist.id}">${playlist.name} <c:if test="${playlist.userDefault}">
				</c:if>
		</a></li>
	</c:forEach>
</ul>