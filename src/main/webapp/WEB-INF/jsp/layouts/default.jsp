<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<!DOCTYPE html>
<html lang="en">
<head>
<script type="text/javascript" src="<c:url value="/jquery/1.7.1/jquery-1.7.1.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/scripts/mashupmedia.js" />"></script>
<script type="text/javascript" src="<c:url value="/jquery-plugins/jquery.address/1.5/jquery.address-1.5.min.js" />"></script>

<script type="text/javascript" src="<c:url value="/jquery-plugins/jquery.jplayer/2.1.0/jquery.jplayer.min.js" />"></script>
<link type="text/css" href="<c:url value="/jquery-plugins/jquery.jplayer/2.1.0/skins/pink.flag/jplayer.pink.flag.css" />" rel="stylesheet" />
 
<link href="<c:url value="${themePath}/stylesheets/site.css"/>" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<c:url value="${themePath}/scripts/theme.js"/>"></script>

<script type="text/javascript">
	$(document).ready(
			function() {
				processBackground("<c:url value="${themePath}/"/>",
						"<tiles:getAsString name="backgroundImageType"/>");
			});
</script>

<title><tiles:getAsString name="title" /></title>

</head>

<body>
	<div id="top-bar">

		<a class="home" href="<c:url value="/" />"><spring:message code="top-bar.home" /></a>

		<ul>
			<li><a href="#">User</a></li>
			<li><a href="#"><spring:message code="top-bar.now-playing" /></a></li>
			<li><a href="#">Sunny</a></li>
			<li><a href="#"><spring:message code="top-bar.log-out" /></a></li>
		</ul>
	</div>

	<img id="background-image" style="display: none;" />

	<div class="panel">
		<c:if test="${fn:length(breadcrumbs) > 1}">
			<div class="breadcrumbs">
				<c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
					<span> <c:choose>
							<c:when test="${status.last}">
								<c:out value="${breadcrumb.name}" />
							</c:when>

							<c:otherwise>
								<a href="<c:url value="${breadcrumb.link}" />"><c:out value="${breadcrumb.name}" /></a> &gt;
							</c:otherwise>

						</c:choose>
					</span>
				</c:forEach>
			</div>

		</c:if>


		<tiles:insertAttribute name="body" />

	</div>


</body>

</html>
