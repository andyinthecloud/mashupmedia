<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
		

</script>


<div class="panel top-panel">
	<ul class="menu">
		<li><a href="<c:url value="/app/music/albums" />">Albums</a></li>
		<li><a href="<c:url value="/app/music/artists" />">Artists</a></li>
	</ul>
</div>

<div class="panel main-panel">
	<tiles:insertAttribute name="body" />
</div>





