<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>



<div class="panel">

Albums


<ul>
<c:forEach items="${albumsPage.albums}" var="album">
<li><a href="<c:url value="/app/music/album/" /><c:out value="${album.id}"/>"><c:out value="${album.name}"/></a></li>
</c:forEach>
</ul>
</div>


