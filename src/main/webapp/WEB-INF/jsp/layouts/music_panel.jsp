<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>



<div class="panel">

	<ul class="menu">
		<li><a href="<c:url value="/app/music/albums" />">Albums</a></li>
		<li><a href="<c:url value="/app/music/artists" />">Artists</a></li>
	</ul>

	<div class="content">
		<tiles:insertAttribute name="body" />
	</div>

</div>


