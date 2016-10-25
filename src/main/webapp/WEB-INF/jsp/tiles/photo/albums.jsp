<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<ul class="main-menu ui-listview-inset ui-corner-all ui-shadow"
	data-role="listview">
	<c:forEach items="${albums}" var="album">
		<li><a rel="internal"
			title="<spring:message code="photo-album.title" /> - ${album.name}"
			href="<c:url value="/app/photo/album/${album.id}"/>">${album.name}</a></li>
	</c:forEach>
</ul>

