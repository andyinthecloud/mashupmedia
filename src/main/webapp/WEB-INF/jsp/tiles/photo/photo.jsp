<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {
        window.scrollTo(0, 0);
        showFooterTabs("photo-photos");

    });
</script>

<c:set var="photo" value="${photoPage.photo}" />


<c:if test="${not empty photo.previousPhoto.id}">
	<div id="photo-previous">
		<a rel="internal" title="${photo.previousPhoto.displayTitle}"
			href="<c:url value="/app/photo/show/${photo.previousPhoto.id}?sequenceType=${photoPage.mediaItemSequenceType}" />"
			class="arrow image-previous"></a>
	</div>
</c:if>

<c:if test="${not empty photo.nextPhoto.id}">
	<div id="photo-next">
		<a rel="internal" title="${photo.nextPhoto.displayTitle}"
			href="<c:url value="/app/photo/show/${photo.nextPhoto.id}?sequenceType=${photoPage.mediaItemSequenceType}" />"
			target="_blank" class="arrow image-next"></a>
	</div>
</c:if>

<div class="photo">
	<h1 class="text">
		<a rel="internal"
			title="<spring:message code="photo-album.title" /> - ${photo.album.name}"
			href="<c:url value="/app/photo/album/${photo.album.id}"/>">${photo.album.name}</a>
		/${photo.displayTitle}
	</h1>

	<ul class="photo-meta text">
		<li><a rel="external"
			href="<c:url value="/app/streaming/media/${photo.id}/original" />"
			target="_blank"><spring:message code="photo.original" /></a></li>
	</ul>


</div>


<div id="full-screen">

	<img alt="${photo.displayTitle}" title="${photo.displayTitle}"
		src="<c:url value="/app/streaming/media/${photo.id}/web_optimised" />" />

</div>

