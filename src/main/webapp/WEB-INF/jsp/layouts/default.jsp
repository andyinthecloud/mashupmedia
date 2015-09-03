<!DOCTYPE html>

<%@page import="org.mashupmedia.constants.MashUpMediaConstants"%>
<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<html>
<head>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">


<sec:csrfMetaTags />



<link rel="stylesheet"
	href="<c:url value="/jquery-mobile/${jQueryMobileVersion}/jquery.mobile-${jQueryMobileVersion}.min.css" />" />
<script
	src="<c:url value="/jquery/${jQueryVersion}/jquery-${jQueryVersion}.min.js" />"></script>

<!-- History.js -->
<script
	src="<c:url value="/jquery-plugins/history/1.8b2/jquery.history.js" />"></script>

<script>
	$(document).on("mobileinit", function() {
		$.mobile.hashListeningEnabled = false;
		$.mobile.pushStateEnabled = false;
		$.mobile.changePage.defaults.changeHash = false;
	});
</script>
<script
	src="<c:url value="/jquery-mobile/${jQueryMobileVersion}/jquery.mobile-${jQueryMobileVersion}.min.js" />"></script>

<link href="<c:url value="${themePath}/stylesheets/site.css"/>"
	rel="stylesheet" type="text/css" />
<link href="<c:url value="${themePath}/stylesheets/site-desktop.css"/>"
	rel="stylesheet" type="text/css" />


<link type="text/css"
	href="<c:url value="/jquery-ui/${jQueryUIVersion}/jquery-ui.min.css" />"
	rel="stylesheet" />

<!-- script type="text/javascript" src="<c:url value="/jquery-ui/${jQueryUIVersion}/jquery-${jQueryVersion}.min.js" />"></script -->

<script type="text/javascript"
	src="<c:url value="/jquery-ui/${jQueryUIVersion}/jquery-ui.min.js" />"></script>

<script type="text/javascript"
	src="<c:url value="/scripts/mashupmedia.jsp" />"></script>


<!-- script type="text/javascript"
	src="<c:url value="/jquery-plugins/jquery.address/${jQueryAddressVersion}/jquery.address-${jQueryAddressVersion}.min.js" />"></script -->

<script type="text/javascript"
	src="<c:url value="/jquery-plugins/jquery.jplayer/${jPlayerVersion}/jplayer/jquery.jplayer.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/jquery-plugins/jquery.jplayer/${jPlayerVersion}/add-on/jplayer.playlist.min.js" />"></script>
<link type="text/css"
	href="<c:url value="/jquery-plugins/jquery.jplayer/${jPlayerVersion}/skin/pink.flag/css/jplayer.pink.flag.min.css" />"
	rel="stylesheet" />

<script type="text/javascript"
	src="<c:url value="/jquery-plugins/datatables/${dataTablesVersion}/jquery.dataTables.min.js" />"></script>



<link href="<c:url value="${themePath}/stylesheets/site.css"/>"
	rel="stylesheet" type="text/css" />
<link href="<c:url value="${themePath}/stylesheets/site-desktop.css"/>"
	rel="stylesheet" type="text/css" />



<!--  script type="text/javascript" src="<c:url value="/scripts/jplayer-android-fix.js" />"></script -->
<script type="text/javascript"
	src="<c:url value="${themePath}/scripts/theme.js"/>"></script>

<script type="text/javascript">
	$(function() {
		// Prepare
		var History = window.History; // Note: We are using a capital H instead of a lower h
		//alert(History.enabled);
		if (!History.enabled) {
			// History.js is disabled for this browser.
			// This is because we can optionally choose to support HTML4 browsers or not.
			return false;
		}

		// Bind to StateChange Event
		History.Adapter.bind(window, 'statechange', function() { // Note: We are using statechange instead of popstate
			var State = History.getState();
			console.log(State);
			var url = State.url;
			var pageType = State.data.pageType;
			if (pageType && pageType == "internal") {
				url = prepareInternalUrlFragment(url);
				$('div.ui-content').load(url);
			} else {				
				window.location.href = url;
			}

		});

		// Capture all the links to push their url to the history stack and trigger the StateChange Event
		$("a.internal").click(function(evt) {

			var title = $(this).attr("title");
			var link = $(this).attr("href");
			evt.preventDefault();
			History.pushState({
				pageType : "internal"
			}, title, link);
		});
	});
	
	function prepareInternalUrlFragment(url) {		
		url = $.trim(url);		
		if (url.indexOf("?") > -1) {
			url += "&";
		} else {
			url += "?";			
		}
		
		url += "fragment=true";
		return url;
	}

	$(document).ready(function() {
		$.mobile.pushStateEnabled = false;

		var jPlayerVersion = "${jPlayerVersion}";
		<c:if test="${isTransparentBackground}">
		$("#contextUrl").val("<c:url value="/" />");
		</c:if>

		$("#log-out").click(function() {
			$("#form-log-out").submit();
		});


	});

</script>

<link rel="stylesheet"
	href="<c:url value="/jquery-plugins/fancybox/2.1.4/jquery.fancybox.css" />"
	type="text/css" media="screen" />
<script type="text/javascript"
	src="<c:url value="/jquery-plugins/fancybox/2.1.4/jquery.fancybox.pack.js" />"></script>

<link rel="icon" type="image/ico"
	href="<c:url value="${themePath}/images/favicon.ico"/>">

<title>${headPageTitle}</title>

</head>

<body class="<tiles:getAsString name="bodyClass"/>">



	<div data-role="page">



		<c:url var="rootUrl" value="/" />
		<form:form id="form-log-out" action="${rootUrl}logout" cssClass="hide"
			data-ajax="false">
			<input type="submit" />
		</form:form>



		<div data-role="header" data-position="fixed">
			<h1>
				<a href="http://www.masupmedia.org"><img alt="Mashup Media"
					title="Mashup Media"
					src="<c:url value="/images/mashupmedia-logo-inline.png" />" /></a>
			</h1>
			<a href="#nav-panel" data-icon="bars" data-iconpos="notext">Menu</a>
			<c:if test="${isNewMashupMediaVersionAvailable}">
				<a href="http://www.mashupmedia.org/download" target="_blank"
					data-icon="info" data-iconpos="notext"
					title="<spring:message code="top-bar.new-update.message" />"></a>
			</c:if>

		</div>


		<div role="main" class="ui-content jqm-content jqm-fullwidth">

			<c:if test="${fn:length(breadcrumbs) > 1}">
				<div class="breadcrumbs">
					<c:forEach items="${breadcrumbs}" var="breadcrumb"
						varStatus="status">
						<span> <c:choose>
								<c:when test="${status.last}">
									<c:out value="${breadcrumb.name}" />
								</c:when>

								<c:otherwise>
									<a href="<c:url value="${breadcrumb.link}" />"><c:out
											value="${breadcrumb.name}" /></a> &gt;
							</c:otherwise>

							</c:choose>
						</span>
					</c:forEach>
				</div>

			</c:if>

			<tiles:insertAttribute name="body" />
		</div>


		<div id="footer" data-role="footer">
			<div class="meta">
				<spring:message code="application.meta"
					arguments="${version},${currentYear}" />
			</div>
		</div>


		<div data-role="panel" data-position-fixed="true" data-display="push"
			data-theme="b" id="nav-panel">

			<ul data-role="listview">
				<li data-icon="delete"><a href="#" data-rel="close"><spring:message
							code="side-menu.close" /></a></li>

				<li><a href="<c:url value="/app/music" />" data-rel="back"
					class="app-link"><spring:message code="top-bar.music" /></a></li>
				<li><a href="<c:url value="/app/videos" />" data-rel="back"><spring:message
							code="top-bar.videos" /></a></li>
				<li><a href="<c:url value="/app/photo/list" />" data-rel="back"><spring:message
							code="top-bar.photos" /></a></li>
				<sec:authorize access="hasRole('ROLE_ADMINISTRATOR')">
					<li><a class="internal" title="Mashup Media - Configuration"
						data-ajax="false" href="<c:url value="/app/configuration" />"><spring:message
								code="home.links.configuration" /></a></li>
					<li><a href="<c:url value="/app/encode/processes" />"
						data-rel="back"><spring:message code="top-bar.encoding.queue" /></a></li>
				</sec:authorize>
				<li><a
					href="<c:url value="/app/configuration/administration/account" />"
					data-rel="back"><spring:message code="top-bar.my-account" /></a></li>
				<li><a id="log-out" href="#" id="log-out"><spring:message
							code="top-bar.log-out" /></a></li>

			</ul>

		</div>




	</div>




</body>

</html>
