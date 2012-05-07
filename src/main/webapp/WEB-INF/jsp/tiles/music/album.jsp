<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>



<div class="panel">

Album


<ul>
<c:forEach items="${albumPage.songs}" var="song">
<li><c:out value="${song.title}"/> </li>
</c:forEach>
</ul>
</div>


