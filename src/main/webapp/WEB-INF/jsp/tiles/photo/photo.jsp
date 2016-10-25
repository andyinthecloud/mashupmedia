<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {

    });
</script>

<c:set var="photo" value="${photoPage.photo }" />

<h1>${photo.album.name}/${photo.displayTitle}</h1>

<c:if test="${not empty photoPage.previousPhoto.id}">
	<div id="photo-previous">
		<a rel="internal" title="${photoPage.previousPhoto.displayTitle}"
			href="<c:url value="/app/photo/show/${photoPage.previousPhoto.id}" />"
			class="arrow image-previous"></a>
	</div>
</c:if>

<c:if test="${not empty photoPage.nextPhoto.id}">
	<div id="photo-next">
		<a rel="internal" title="${photoPage.nextPhoto.displayTitle}"
			href="<c:url value="/app/photo/show/${photoPage.nextPhoto.id}" />"
			target="_blank" class="arrow image-next"></a>
	</div>
</c:if>

<div class="photo">
	<img alt="${photo.displayTitle}" title="${photo.displayTitle}"
		src="<c:url value="/app/streaming/media/${photo.id}/web_optimised" />" />
</div>

<ul class="items">
	<li><a rel="external"
		href="<c:url value="/app/streaming/media/${photo.id}/original" />"><spring:message
				code="photo.original" /></a></li>

</ul>

