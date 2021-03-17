<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<c:if test="${!isAppend}">
	<script type="text/javascript">
        $(document).ready(function() {
            window.scrollTo(0, 0);

            $(window).scroll(function() {
                if ($("ul.photos li").length == 0) { return; }

                var url = History.getState().url;

                if (url.indexOf("photos") > 0) {
                    //appendContentsOnScroll("${photoListType.className}");    
                    appendContentsOnScroll("photo-list-latest");
                }

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

			<li>
				<h2 class="text">${photo.displayTitle}</h2> <img
				alt="${photo.displayTitle}" title="${photo.displayTitle}"
				src="<c:url value="/streaming/media/${photo.id}/web_optimised" />" />

				<ul class="photo-meta text">
					<li><a rel="external"
						href="<c:url value="/streaming/media/${photo.id}/original" />"
						target="_blank"><spring:message code="photo.original" /></a></li>
				</ul>
			</li>

		</c:forEach>
		<c:if test="${!isAppend}">
			</ul>
		</c:if>
	</c:otherwise>
</c:choose>
