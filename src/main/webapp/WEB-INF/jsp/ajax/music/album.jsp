<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<div class="sub-panel">

<h1><c:out value="${albumPage.album.artist.name}" /> - <c:out value="${albumPage.album.name}" /></h1>

<ul class="album-menu">
<c:forEach items="${albumPage.songs}" var="song">
<li><div class="meta"><c:out value="${song.meta}"/></div><c:out value="${song.trackNumber}"/> - <c:out value="${song.title}"/></li>
</c:forEach>
</ul>

</div>


