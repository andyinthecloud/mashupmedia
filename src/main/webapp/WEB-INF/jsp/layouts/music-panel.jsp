<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		$("div.random-album-art div.album").hover(function() {			
			$(this).addClass("highlight");
		}, function() {
			$(this).removeClass("highlight");
		});

	});
</script>

<div class="panel">

	<ul class="category-menu">
		<li><a href="<c:url value="/app/music/albums" />">Albums</a></li>
		<li><a href="<c:url value="/app/music/artists" />">Artists</a></li>
	</ul>

	<div class="items">
		<tiles:insertAttribute name="body" />
	</div>

	<div style="clear: both;">&nbsp;</div>


</div>

