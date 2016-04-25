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


<script type="text/javascript"
	src="<c:url value="/jquery-ui/${jQueryUIVersion}/jquery-ui.min.js" />"></script>

<script type="text/javascript"
	src="<c:url value="/jquery-ui/touch-punch/jquery.ui.touch-punch.min.js" />"></script>


<script type="text/javascript"
	src="<c:url value="/scripts/mashupmedia.jsp" />"></script>


<!-- script type="text/javascript"
	src="<c:url value="/jquery-plugins/jquery.address/${jQueryAddressVersion}/jquery.address-${jQueryAddressVersion}.min.js" />"></script -->

<script type="text/javascript"
	src="<c:url value="/jquery-plugins/jquery.jplayer/${jPlayerVersion}/jplayer/jquery.jplayer.min.js" />"></script>

<script type="text/javascript"
	src="<c:url value="/scripts/jplayer-android-fix.js" />"></script>




<!-- script type="text/javascript"
	src="<c:url value="/jquery-plugins/jquery.jplayer/${jPlayerVersion}/add-on/jplayer.playlist.min.js" />"></script -->


<!-- link type="text/css"
	href="<c:url value="/jquery-plugins/jquery.jplayer/${jPlayerVersion}/skin/pink.flag/css/jplayer.pink.flag.min.css" />"
	rel="stylesheet" / -->


<!-- script type="text/javascript"
	src="<c:url value="/jquery-plugins/datatables/${dataTablesVersion}/jquery.dataTables.min.js" />"></script -->

<script
	src="<c:url value="/jquery-plugins/jeditable/1.7.1/jquery.jeditable.min.js" />"
	type="text/javascript" charset="utf-8"></script>

<!--  script type="text/javascript" src="<c:url value="/scripts/jplayer-android-fix.js" />"></script -->
<script type="text/javascript"
	src="<c:url value="${themePath}/scripts/theme.js"/>"></script>

<script type="text/javascript">
    $(function() {
        // Prepare
        var History = window.History; // Note: We are using a capital H instead of a lower h
        if (!History.enabled) {
            // History.js is disabled for this browser.
            // This is because we can optionally choose to support HTML4 browsers or not.
            return false;
        }

        // Bind to StateChange Event
        History.Adapter.bind(window, 'statechange', function() { // Note: We are using statechange instead of popstate
            var State = History.getState();
            // console.log(State);
            var url = State.url;
            var pageType = State.data.pageType;
            if (pageType && pageType == "internal") {
                url = prepareInternalUrlFragment(url);
                $.get(url, function(data) {
                    var uiContentElement = $("div.ui-content div.dynamic-content");
                    uiContentElement.html(data);
                    uiContentElement.enhanceWithin();
                });

            } else {
                window.location.href = url;
            }

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
        // Unbind declared event handlers
	    $("body").off("click", "a[rel='internal']");

        
        // Capture all the links to push their url to the history stack and trigger the StateChange Event
        $("body").on("click", "a[rel='internal']", function(event) {            
            var pageTitlePrefix = "<spring:message code="page.default.title.prefix" />";
            var title = pageTitlePrefix + " " + $(this).attr("title");
            var link = $(this).attr("href");
            var mediaType = $(this).attr("data-media");
            showFooterTabs(mediaType);

            event.preventDefault();
            History.pushState({
                pageType: "internal"
            }, title, link);

        });        

        var jPlayerVersion = "${jPlayerVersion}";
        <c:if test="${isTransparentBackground}">
        $("#contextUrl").val("<c:url value="/" />");
        </c:if>

        $("#log-out").click(function() {
            $("#form-log-out").submit();
        });

        $("#nav-panel").on("click", "a", function() {
            $("#nav-panel a[data-rel=close]").click();
        });

        document.title = "${headPageTitle}";

        $("#music-player").on("click", ".controls a.play", function() {
            togglePlayPause("play");
            myAndroidFix.play();
        });

        $("#music-player").on("click", ".controls a.pause", function() {
            togglePlayPause("pause");
            $(mashupMedia.jPlayerId).jPlayer("pause");
        });

        $("#music-player").on("click", ".controls a.stop", function() {
            togglePlayPause("stop");
            $(mashupMedia.jPlayerId).jPlayer("stop");
        });

        $("#music-player").on("click", ".controls a.previous", function() {
            mashupMedia.playPreviousSong();
        });

        $("#music-player").on("click", ".controls a.next", function() {
            mashupMedia.playNextSong();
        });

        mashupMedia.loadLastAccessedPlaylist();
        
        mashupMedia.showMessage("andy message bix");
    });

    function togglePlayPause(action) {
        action = action.toLowerCase();
        var imagePath = null;

        var text = null;
        var nextAction = null;

        if (action == "play") {
            nextAction = "pause";
            imagePath = "<c:url value="${themePath}/images/media-player/pause.png"/>";
            text = "<spring:message code="action.pause"/>";
        } else if (action == "pause") {
            nextAction = "play";
            imagePath = "<c:url value="${themePath}/images/media-player/play.png"/>";
            text = "<spring:message code="action.play"/>";
        } else if (action == "stop") {
            nextAction = "play";
            imagePath = "<c:url value="${themePath}/images/media-player/play.png"/>";
            text = "<spring:message code="action.stop"/>";
        }

        var controlElement = $("#music-player .controls a." + action);
        var imageElement = controlElement.find("img");
        imageElement.attr("src", imagePath);
        imageElement.attr("alt", text);
        imageElement.attr("title", text);
        controlElement.removeClass();
        controlElement.addClass(nextAction);
    }

    function showFooterTabs(mediaType, highlight) {

        if (mediaType === undefined) {
            $("#footer").hide();
            return;
        }

        $("#footer div.tabs").hide();

        var isShowFooter = false;

        mediaType = mediaType.toLowerCase();

        if (mediaType == "music") {
            $("#footer div.music").show();
            isShowFooter = true;
        } else if (mediaType == "photos") {
            isShowFooter = true;

        }

        if (isShowFooter) {
            $("#footer").show();
        } else {
            $("#footer").hide();
        }

        $("#footer div.music li a").removeClass("ui-btn-active");
        $("#footer div.music li a." + highlight).addClass("ui-btn-active");

    }
</script>

<!-- 
<link rel="stylesheet"
	href="<c:url value="/jquery-plugins/fancybox/2.1.4/jquery.fancybox.css" />"
	type="text/css" media="screen" />
<script type="text/javascript"
	src="<c:url value="/jquery-plugins/fancybox/2.1.4/jquery.fancybox.pack.js" />"></script>
 -->

<link rel="stylesheet" href="<c:url value="/jquery-plugins/swipebox/1.4.1/swipebox.css" />">
<script src="<c:url value="/jquery-plugins/swipebox/1.4.1/jquery.swipebox.js" />"></script>
	

<link rel="icon" type="image/ico"
	href="<c:url value="${themePath}/images/favicon.ico"/>">

<title>${headPageTitle}</title>

</head>

<body class="<tiles:getAsString name="bodyClass"/>">

	<div id="jquery_jplayer_1" class="jp-jplayer"></div>


	<div data-role="page">



		<c:url var="rootUrl" value="/" />
		<form:form id="form-log-out" action="${rootUrl}logout" cssClass="hide"
			data-ajax="false">
			<input type="submit" />
		</form:form>



		<div data-role="header" data-position="fixed" id="header"
			data-tap-toggle="false">

			<a class="ui-btn-left" id="logo" href="<c:url value="/" />"
				rel="internal" title="<spring:message code="home.title" />"><img
				class="logo-inline" alt="Mashup Media" title="Mashup Media"
				src="<c:url value="/images/mashupmedia-logo-inline.png" />" /></a> <a
				class="ui-btn-right" href="#nav-panel" data-icon="bars"
				data-iconpos="notext">Menu</a>

			<div id="music-player">
				<table>
					<tr>
						<td class="controls"><a class="previous" href="javascript:;"><img
								title="<spring:message code="action.previous"/>"
								alt="<spring:message code="action.previous"/>"
								src="<c:url value="${themePath}/images/media-player/previous.png"/>" /></a>
							<a class="play" href="javascript:;"><img
								title="<spring:message code="action.play"/>"
								alt="<spring:message code="action.play"/>"
								src="<c:url value="${themePath}/images/media-player/play.png"/>" /></a>
							<a class="next" href="javascript:;"><img
								title="<spring:message code="action.next"/>"
								alt="<spring:message code="action.next"/>"
								src="<c:url value="${themePath}/images/media-player/next.png"/>" /></a>
						</td>
						<td class="album-art"></td>
						<td>
							<div class="artist-name"></div>
							<div class="title"></div>
						</td>
					</tr>

				</table>



				<!-- 
				<a class="pause"><img title="<spring:message code="action.play"/>" alt="<spring:message code="action.play"/>" src="<c:url value="${themePath}/images/media-player/play.png"/>" /></a>
				 
				<button class="stop">Stop</button>
				-->
				<div class="progress">
					<div class="play-bar"></div>
				</div>
			</div>

		</div>


		<div role="main" class="ui-content jqm-content jqm-fullwidth">
		
			<div id="information-box"></div>

			<div class="dynamic-content">
				<c:if test="${fn:length(breadcrumbs) > 1}">
					<div class="breadcrumbs">
						<c:forEach items="${breadcrumbs}" var="breadcrumb"
							varStatus="status">
							<span> <c:choose>
									<c:when test="${status.last}">
										<c:out value="${breadcrumb.name}" />
									</c:when>

									<c:otherwise>
										<a href="<c:url value="${breadcrumb.link}" />" rel="internal"
											title="${breadcrumb.name}"><c:out
												value="${breadcrumb.name}" /></a> &gt;
								</c:otherwise>

								</c:choose>
							</span>
						</c:forEach>
					</div>

				</c:if>

				<div class="main-content">
					<tiles:insertAttribute name="body" />
				</div>
			</div>

			<div class="footer-meta">
				<spring:message code="application.meta"
					arguments="${version},${currentYear}" />
			</div>

		</div>


		<div id="footer"
			class="ui-footer ui-bar-inherit ui-footer-fixed slideup"
			data-role="footer" data-position-fixed="true">
			<div class="tabs music" data-role="navbar">
				<ul>

					<li><a rel="internal" href="<c:url value="/app/music/random-albums" />" class="music-albums"
						data-media="music"
						title="<spring:message code="footer.music.albums"/>"><img
							alt="<spring:message code="footer.music.albums"/>"
							src="<c:url value="${themePath}/images/link-icons/album.png"/>" /></a></li>

					<li><a rel="internal"
						href="<c:url value="/app/music/artists" />" class="music-artists"
						data-media="music"
						title="<spring:message code="footer.music.artists"/>"><img
							alt="<spring:message code="footer.music.artists"/>"
							src="<c:url value="${themePath}/images/link-icons/artist.png"/>" /></a></li>

					<li><a rel="internal"
						href="<c:url value="/app/playlist/music" />"
						class="music-playlist" data-media="music"
						title="<spring:message code="footer.music.playlist"/>"><img
							alt="<spring:message code="footer.music.playlist"/>"
							src="<c:url value="${themePath}/images/link-icons/playlist.png"/>" /></a></li>
				</ul>
			</div>
		</div>

		<div data-role="panel" data-position="right"
			data-position-fixed="true" data-display="push" data-theme="b"
			id="nav-panel">

			<ul data-role="listview">
				<li data-icon="delete"><a href="#" data-rel="close"><spring:message
							code="side-menu.close" /></a></li>
				<li><a rel="internal"
					title="<spring:message code="home.title" /> "
					href="<c:url value="/" />"><spring:message code="top-bar.home" /></a></li>

				<li><a rel="internal"
					title="<spring:message code="music.title" />"
					href="<c:url value="/app/music/random-albums" />"
					data-media="music"><spring:message code="top-bar.music" /></a></li>
				<li><a href="<c:url value="/app/videos" />"><spring:message
							code="top-bar.videos" /></a></li>
				<li><a href="<c:url value="/app/photo/list" />"><spring:message
							code="top-bar.photos" /></a></li>
				<sec:authorize access="hasRole('ROLE_ADMINISTRATOR')">
					<li><a rel="internal"
						title="<spring:message code ="configuration.title" />"
						data-ajax="false" href="<c:url value="/app/configuration" />"><spring:message
								code="home.links.configuration" /></a></li>
					<li><a rel="internal"
						title="<spring:message code="encoding-processes.title" />"
						href="<c:url value="/app/encode/processes" />"><spring:message
								code="top-bar.encoding.queue" /></a></li>
				</sec:authorize>
				<li><a rel="internal"
					title="<spring:message code="configuration.administration.my-account.title" />"
					href="<c:url value="/app/configuration/administration/my-account" />"><spring:message
							code="top-bar.my-account" /></a></li>

				<c:if test="${isNewMashupMediaVersionAvailable}">
					<li><a href="http://www.mashupmedia.org/download"
						target="_blank"
						title="<spring:message code="top-bar.new-update.message" />"></a></li>
				</c:if>


				<li><a id="log-out" href="#" id="log-out"><spring:message
							code="top-bar.log-out" /></a></li>

			</ul>

		</div>




	</div>




</body>

</html>
