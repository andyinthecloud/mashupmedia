<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/inc/jquery.jsp" />


<link href="<c:url value="${themePath}/stylesheets/site.css"/>" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<c:url value="${themePath}/scripts/theme.js"/>"></script>

<script type="text/javascript">
	$(window)
			.load(
					function() {
						var theWindow = $(window), 
						$backgroundImage = $("#background-image"), 
						aspectRatio = $backgroundImage.width() / $backgroundImage.height();

						function resizeBackgroundImage() {

							if ((theWindow.width() / theWindow.height()) < aspectRatio) {
								$backgroundImage.removeClass().addClass(
										'full-height');
							} else {
								$backgroundImage.removeClass().addClass(
										'full-width');
							}

						}

						theWindow.resize(function() {
							resizeBackgroundImage();
						}).trigger("resize");

					});

	$(document)
			.ready(
					function() {
						var backgroundImagePath = getBackgroundImage("<tiles:getAsString name="backgroundImageType"/>");
						$("#background-image").attr(
								"src",
								"<c:url value="${themePath}/"/>"
										+ backgroundImagePath);

					});
</script>

<title><tiles:getAsString name="title" /></title>

</head>

<body>


	<img id="background-image" src="<c:url value="/images/default-background.png" />" />


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
