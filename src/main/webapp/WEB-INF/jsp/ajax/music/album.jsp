<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		$("#play-all").click(function() {
			 var albumId = $(this).closest("ul").attr("id");
			 alert(albumId);
		});
		
	});
</script>


<div class="sub-panel album">

	<h1>
		<a id="artistId-<c:out value="${albumPage.album.artist.id}" />" href="javascript:void(0);"><c:out
				value="${albumPage.album.artist.name}" /></a> -
		<c:out value="${albumPage.album.name}" />
	</h1>




	<ul class="control-menu" id="albumId-<c:out value="${albumPage.album.id}" />">
		<li class="first"><a href="javascript:void(0);" id="play-all" > <spring:message code="action.play-all" />
		</a></li>

		<li><a href="javascript:void(0);" id="add-all"> <spring:message code="action.add-all" />
		</a></li>

	</ul>



	<div class="album-art">
		<img src="<c:url value="/app/music/album-art/${albumPage.album.id}" />"
			title="<c:out value="${albumPage.album.artist.name}" /> - <c:out value="${albumPage.album.name}" />"
			alt="<c:out value="${albumPage.album.artist.name}" /> - <c:out value="${albumPage.album.name}" />" />
	</div>

	<ul class="album-menu">
		<c:forEach items="${albumPage.songs}" var="song">
			<li id="songId-<c:out value="${song.id}"/>">
				<div class="controls">
					<a class="play" href="javascript:void(0);"><img
						src="<c:url value="${themePath}/images/controls/play.png" />"
						title="<spring:message code="control.play" />" /></a> <a class="add" href="javascript:void(0);"><img
						src="<c:url value="${themePath}/images/controls/add.png" />"
						title="<spring:message code="control.add" />" /></a>
				</div> <c:out value="${song.trackNumber}" /> - <c:out value="${song.title}" />

				<div class="meta">
					<c:out value="${song.meta}" />
				</div>

			</li>
		</c:forEach>
	</ul>





</div>

