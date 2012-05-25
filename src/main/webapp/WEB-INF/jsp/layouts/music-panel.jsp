<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	
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

