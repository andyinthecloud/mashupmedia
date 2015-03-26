<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div class="sub-panel">

	<h1>${photo.album.name} - ${photo.displayTitle}</h1>

	<img class="original-photo" alt="${photo.displayTitle}" title="${photo.displayTitle}"
		src="<c:url value="/app/photo/original/${photo.id}" />" />

	<p>${photo.metadata}</p>

</div>