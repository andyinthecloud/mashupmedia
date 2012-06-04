<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(
			function() {
				$("div.random-album-art div.album").hover(function() {
					$(this).addClass("highlight");
				}, function() {
					$(this).removeClass("highlight");
				});

				$("div.random-album-art div.album a").click(
						function() {
							var albumId = $(this).attr("id");
							albumId = albumId.replace("albumId-", "");
							$.get("<c:url value="/app/ajax/music/album/" />"
									+ albumId, function(data) {
								$("div.panel div.content").html(data);
							});
							
						});

			});
</script>


<div class="random-album-art">
	<c:forEach items="${albums}" var="album">
		<div class="album">
			<a id="albumId-<c:out value="${album.id}" />" href="javascript:void(0);"> <img
				src="<c:url value="/app/music/album-art/${album.id}" />"
				title="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />"
				alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
			</a>
		</div>
	</c:forEach>
</div>

