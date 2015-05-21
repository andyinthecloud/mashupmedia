<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<script type="text/javascript">
	$(document).ready(function() {
		mashupMedia.filterPageNumber = 0;

		$.get("<c:url value="/app/ajax/photo/load-latest-photos" />", {
			pageNumber : 0
		}, function(data) {
			$("body.photo div.sub-panel ul.photo-thumbnails").append(data);
		});
		
		$("body.photo ul.photo-thumbnails").on("mouseover", "li a", function() {
			$(this).find("img").addClass("photo-highlight");
		});

		$("body.photo ul.photo-thumbnails").on("mouseout", "li a", function() {
			$(this).find("img").removeClass("photo-highlight");
		});

	});

	$(window).scroll(function() {
		currentPage = addressListPhotos;
		appendContentsOnScroll();
	});
</script>

<div class="sub-panel">

	<ul class="photo-thumbnails">

	</ul>


</div>