<!DOCTYPE html> 

<%@page import="org.mashupmedia.constants.MashUpMediaConstants"%>
<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<html lang="en">
<head>

<sec:csrfMetaTags/>

<link type="text/css" href="<c:url value="/jquery-ui/${jQueryUIVersion}/jquery-ui.min.css" />" rel="stylesheet" />
<script type="text/javascript" src="<c:url value="/jquery-ui/${jQueryUIVersion}/jquery-${jQueryVersion}.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/jquery-ui/${jQueryUIVersion}/jquery-ui.min.js" />"></script>

<script type="text/javascript" src="<c:url value="/scripts/mashupmedia.jsp" />"></script>

<script type="text/javascript" src="<c:url value="/jquery-plugins/jquery.address/${jQueryAddressVersion}/jquery.address-${jQueryAddressVersion}.min.js" />"></script>

<script type="text/javascript" src="<c:url value="/jquery-plugins/jquery.jplayer/${jPlayerVersion}/jplayer/jquery.jplayer.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/jquery-plugins/jquery.jplayer/${jPlayerVersion}/add-on/jplayer.playlist.min.js" />"></script>
<link type="text/css" href="<c:url value="/jquery-plugins/jquery.jplayer/${jPlayerVersion}/skin/pink.flag/css/jplayer.pink.flag.min.css" />" rel="stylesheet" />

<script type="text/javascript" src="<c:url value="/jquery-plugins/datatables/${dataTablesVersion}/jquery.dataTables.min.js" />"></script>



<link href="<c:url value="${themePath}/stylesheets/site.css"/>" rel="stylesheet" type="text/css" />
<!--  script type="text/javascript" src="<c:url value="/scripts/jplayer-android-fix.js" />"></script -->
<script type="text/javascript" src="<c:url value="${themePath}/scripts/theme.js"/>"></script>

<script type="text/javascript">
	$(document).ready(
			function() {
				var jPlayerVersion = "${jPlayerVersion}";
				<c:if test="${isTransparentBackground}">
				$("#contextUrl").val("<c:url value="/" />");
				processBackground("<c:url value="${themePath}/"/>",
						"<tiles:getAsString name="backgroundImageType"/>");
				</c:if>
			});
</script>

<link rel="stylesheet" href="<c:url value="/jquery-plugins/fancybox/2.1.4/jquery.fancybox.css" />" type="text/css" media="screen" />
<script type="text/javascript" src="<c:url value="/jquery-plugins/fancybox/2.1.4/jquery.fancybox.pack.js" />"></script>

<link rel="icon" type="image/ico" href="<c:url value="${themePath}/images/favicon.ico"/>">

<title>${headPageTitle}</title>

</head>

<body class="<tiles:getAsString name="bodyClass"/>">

<c:url var="rootUrl" value="/" />
<form:form id="form-log-out"  action="${rootUrl}logout" cssClass="hide">
<input type="submit" />
</form:form>

	<tiles:insertAttribute name="topBar" />

	<input type="hidden" id="contextUrl" />
	<img id="background-image" style="display: none;" />

	<div class="panel big-rounded-corners">
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

	<div id="footer" class="transparent">
		<div class="meta small-rounded-corners">
			<spring:message code="application.meta" arguments="${version},${currentYear}" />
		</div>
	</div>

</body>

</html>
