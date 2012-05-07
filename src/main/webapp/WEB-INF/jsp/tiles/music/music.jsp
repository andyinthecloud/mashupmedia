<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>



<div class="panel">

<c:forEach items="${musicPage.albums}" var="album">

<img src="<c:url value="/app/music/album-art/${album.id}" />" />
<br/>

</c:forEach>

</div>


