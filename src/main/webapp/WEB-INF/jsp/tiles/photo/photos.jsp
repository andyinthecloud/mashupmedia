<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<c:if test="${!isAppend}">
	<script type="text/javascript">
        $(document).ready(function() {
            window.scrollTo(0, 0);
            $(window).scroll(function() {
                if ($("ul.photos li").length == 0) { return; }
                appendContentsOnScroll("${photoListType.className}");
            });

            showFooterTabs("photo-photos");

        });
    </script>

</c:if>


<c:set var="sequence" value="latest" />

<c:if test="${!empty album && !isAppend}">
	<h1>${album.name}</h1>
	<c:set var="sequence" value="photo_album" />
</c:if>

<c:choose>
	<c:when test="${fn:length(photos) == 0 && !isAppend}">
		<spring:message code="photos.empty" />
	</c:when>
	<c:otherwise>
		<c:if test="${!isAppend}">
			<ul class="photos">
		</c:if>
		<c:forEach items="${photos}" var="photo">
			<li class="photo"><a rel="internal"
				title="${photo.displayTitle}"
				href="<c:url value="/app/photo/show/${photo.id}?sequenceType=${sequence}" />">
					<img alt="${photo.displayTitle}" title="${photo.displayTitle}"
					src="<c:url value="/app/streaming/media/${photo.id}/thumbnail" />" />
			</a></li>
		</c:forEach>
		<c:if test="${!isAppend}">
			</ul>
		</c:if>
	</c:otherwise>
</c:choose>
