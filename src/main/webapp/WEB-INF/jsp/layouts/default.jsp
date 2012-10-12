<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<!DOCTYPE html>
<html lang="en">
<head>

<link type="text/css"
	href="<c:url value="/jquery-ui/1.8.21/css/smoothness/jquery-ui-1.8.21.custom.css" />"
	rel="Stylesheet" />
<script type="text/javascript" src="<c:url value="/jquery-ui/1.8.21/js/jquery-1.7.2.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/jquery-ui/1.8.21/js/jquery-ui-1.8.21.custom.min.js" />"></script>

<script type="text/javascript" src="<c:url value="/scripts/mashupmedia.jsp" />"></script>
<script type="text/javascript"
	src="<c:url value="/jquery-plugins/jquery.address/1.5/jquery.address-1.5.min.js" />"></script>

<script type="text/javascript"
	src="<c:url value="/jquery-plugins/jquery.jplayer/2.2.0/jquery.jplayer.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/jquery-plugins/jquery.jplayer/2.2.0/add-on/jplayer.playlist.min.js" />"></script>
<link type="text/css"
	href="<c:url value="/jquery-plugins/jquery.jplayer/2.2.0/skins/pink.flag/jplayer.pink.flag.css" />"
	rel="stylesheet" />

<script type="text/javascript"
	src="<c:url value="/jquery-plugins/datatables/1.9.1/jquery.dataTables.min.js" />"></script>
	


<link href="<c:url value="${themePath}/stylesheets/site.css"/>" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<c:url value="${themePath}/scripts/theme.js"/>"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$("#contextUrl").val("<c:url value="/" />");
		processBackground("<c:url value="${themePath}/"/>",	"<tiles:getAsString name="backgroundImageType"/>");
	});
</script>

<title><tiles:getAsString name="title" /></title>

</head>

<body>
	<tiles:insertAttribute name="topBar" />
	<input type="hidden" id="contextUrl"  />

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
