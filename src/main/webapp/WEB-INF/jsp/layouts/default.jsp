<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0//EN">
<html lang="en">
<head>

<c:set var="jQueryUIVersion" value="1.10.1"/>
<c:set var="jQueryVersion" value="1.9.1"/>
<c:set var="jQueryAddress" value="1.6"/>
<c:set var="jPlayer" value="2.2.0"/>
<c:set var="dataTables" value="1.9.1"/>


<link type="text/css" href="<c:url value="/jquery-ui/${jQueryUIVersion}/css/smoothness/jquery-ui-${jQueryUIVersion}.custom.css" />" rel="stylesheet" />
<script type="text/javascript" src="<c:url value="/jquery-ui/${jQueryUIVersion}/js/jquery-${jQueryVersion}.min.js" />"></script>

<!-- 
<script src="http://code.jquery.com/jquery-migrate-1.1.0.js"></script>
 -->
   
<script type="text/javascript" src="<c:url value="/jquery-ui/${jQueryUIVersion}/js/jquery-ui-${jQueryUIVersion}.custom.min.js" />"></script>

<script type="text/javascript" src="<c:url value="/scripts/mashupmedia.jsp" />"></script>
<script type="text/javascript" src="<c:url value="/jquery-plugins/jquery.address/${jQueryAddress}/jquery.address-${jQueryAddress}.min.js" />"></script>

<script type="text/javascript" src="<c:url value="/jquery-plugins/jquery.jplayer/${jPlayer}/jquery.jplayer.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/jquery-plugins/jquery.jplayer/${jPlayer}/add-on/jplayer.playlist.min.js" />"></script>
<link type="text/css" href="<c:url value="/jquery-plugins/jquery.jplayer/${jPlayer}/skins/pink.flag/jplayer.pink.flag.css" />" rel="stylesheet" />

<script type="text/javascript" src="<c:url value="/jquery-plugins/datatables/${dataTables}/jquery.dataTables.min.js" />"></script>



<link href="<c:url value="${themePath}/stylesheets/site.css"/>" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<c:url value="${themePath}/scripts/theme.js"/>"></script>

<script type="text/javascript">
	$(document).ready(
			function() {
				$("#contextUrl").val("<c:url value="/" />");
				processBackground("<c:url value="${themePath}/"/>",
						"<tiles:getAsString name="backgroundImageType"/>");
			});
</script>

<link rel="stylesheet" href="<c:url value="/jquery-plugins/nivo-slider/3.2/nivo-slider.css" />" type="text/css" media="screen" />
<link rel="stylesheet" href="<c:url value="/jquery-plugins/nivo-slider/3.2/themes/dark/dark.css" />" type="text/css" media="screen" />
<script src="<c:url value="/jquery-plugins/nivo-slider/3.2/jquery.nivo.slider.pack.js" />" type="text/javascript"></script>

<link rel="stylesheet" href="<c:url value="/jquery-plugins/fancybox/2.1.4/jquery.fancybox.css" />" type="text/css" media="screen" />
<script type="text/javascript" src="<c:url value="/jquery-plugins/fancybox/2.1.4/jquery.fancybox.pack.js" />"></script>



<title><tiles:getAsString name="title" /></title>

</head>

<body class="<tiles:getAsString name="bodyClass"/>">

	<tiles:insertAttribute name="topBar" />
	
		<input type="hidden" id="contextUrl" /> <img id="background-image" style="display: none;" />

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
