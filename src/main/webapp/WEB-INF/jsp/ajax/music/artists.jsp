<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


Artists

<ul>
<c:forEach items="${artistsPage.artists}" var="artist">
<li><c:out value="${artist.name}"/> </li>
</c:forEach>
</ul>





