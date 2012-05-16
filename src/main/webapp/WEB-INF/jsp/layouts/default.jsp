<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/inc/jquery.jsp" />


<link href="<c:url value="${themePath}/stylesheets/site.css"/>" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<c:url value="${themePath}/scripts/theme.js"/>"></script>

<script type="text/javascript">

$(window).load(function() {
	var aspectRatio = $("#background-image").width() / $("#background-image").height();
	$(this).resize(function() {
		resizeBackgroundImage(aspectRatio);
	}).trigger("resize");
});


function resizeBackgroundImage(aspectRatio) {
	
	if (($(window).width() / $(window).height()) < aspectRatio) {
		$("#background-image").removeClass().addClass("full-height");
	} else {
		$("#background-image").removeClass().addClass('full-width');
	}
}
	
</script>

<title><tiles:getAsString name="title" /></title>

</head>

<body>

	<img id="background-image" src="<c:url value="${themePath}/images/default/background-01.jpg"/>" />


	<div class="panel breadcrumbs">
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

	<tiles:insertAttribute name="body" />

</body>

</html>
