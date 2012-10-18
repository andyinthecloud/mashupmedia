<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<h1>
	<spring:message code="music.artists.title" />
</h1>

<ul class="index-letters">
	<c:forEach items="${artistsPage.artistIndexLetters}" var="letter">
		<li><a href="javascript:;"><c:out value="${letter}" /></a></li>
	</c:forEach>
</ul>

<ul class="main-menu">
	<c:forEach items="${artistsPage.artists}" var="artist">
		<li><a href="javascript:;" id="artist-id-${artist.id}"><c:out value="${artist.name}" /></a></li>
	</c:forEach>
</ul>


