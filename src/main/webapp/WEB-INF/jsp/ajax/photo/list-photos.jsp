<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<c:forEach items="${photos}" var="photo">
	<li><a href="<c:url value="/photo/show/${photo.id}/" />">
			<img alt="${photo.displayTitle}" title="${photo.displayTitle}"
			src="<c:url value="/photo/thumbnail/${photo.id}" />" />
	</a></li>
</c:forEach>
