<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<script type="text/javascript">

	$(document).ready(function() {
		
		
		
		$("body.photo ul.photo-thumbnails li a" ).hover(
		  function() {
		    $(this).find("img").addClass("photo-highlight");
		  }, function() {
			  $(this).find("img").removeClass("photo-highlight");
		  }
		);

	});
	
	$(window).scroll(function() {
/*
		if ($("div.random-album-art div.album").length == 0) {
			return;
		}
*/
		currentPage = addressListPhotos; 
		appendContentsOnScroll();
	});
	

</script>

<div class="sub-panel">

	<c:choose>
		<c:when test="${fn:length(photos) == 0}">
			<spring:message code="list-photos.empty" />
		</c:when>

		<c:otherwise>
			<ul class="photo-thumbnails">
				<c:forEach items="${photos}" var="photo">
					<li><a href="<c:url value="/app/photo/show/${photo.id}/" />">
							<img alt="${photo.displayTitle}" title="${photo.displayTitle}"
							src="<c:url value="/app/photo/thumbnail/${photo.id}" />" />

					</a></li>
				</c:forEach>
			</ul>
		</c:otherwise>
	</c:choose>
</div>