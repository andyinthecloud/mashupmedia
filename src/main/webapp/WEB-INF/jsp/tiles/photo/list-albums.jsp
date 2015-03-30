<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<h1>
	<spring:message code="photo-albums.title" />
</h1>



<ul class="main-menu">
	<c:forEach items="${albums}" var="album">
		<li><a href="<c:url value="/app/photo/album/show/${album.id}"/>"><c:out value="${album.name}" /></a></li>
	</c:forEach>
</ul>

