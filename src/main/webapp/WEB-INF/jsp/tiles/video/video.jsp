<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
$(document).ready(function(){

	$("#jquery_jplayer_1").jPlayer({
		ready: function () {
			$(this).jPlayer("setMedia", {
				
				<c:set var="videoFormats" value="" />

				<c:forEach items="${videoPage.video.mediaEncodings}" var="mediaEncoding">
					<c:set var="videoFormat" value="${mediaEncoding.mediaContentType.jPlayerContentType}" />					
					${videoFormat}: "<c:url value="/app/streaming/media/${videoPage.video.id}?mediaContentType=${videoFormat}" />",							
					<c:if test="not empty videoFormats">
						<c:set var="videoFormat" value=",${videoFormat}" />
					</c:if>
					<c:set var="videoFormats" value="${videoFormats}${videoFormat}" />
							
				</c:forEach>
				
				poster: "<c:url value="${videoPage.posterUrl}" />"				
			});
		},
		swfPath: "<c:url value="/jquery-plugins/jquery.jplayer/${jPlayerVersion}" />",
		supplied: "${videoFormats}",
		size: {
			width: "640px",
			height: "360px",
			cssClass: "jp-video-360p"
		},
		smoothPlayBar: true,
		keyEnabled: true
	});
});
</script>

<jsp:include page="/WEB-INF/jsp/inc/remote-video-info-js.jsp" />

<div class="sub-panel">

	<h1>
		<c:out value="${videoPage.video.displayTitle}" />
	</h1>

	<div id="remote">
		<a class="arrow-show-hide" href="javascript:void(0)"> <img src="<c:url value="/images/arrow-down.png" />" /></a>
		<div class="profile">${videoPage.video.summary}</div>
		<div class="images"></div>

		<div class="disclaimer">
			<spring:message code="music.artists.remote" />
			<a href="http://www.last.fm" target="_blank" title=""><img title="last.fm" src="<c:url value="/images/lastfm.png" />" /></a>. <a class="incorrect"
				href="javascript:;"><spring:message code="music.artists.remote.correct" /></a> | <a
				href="<c:url value="/app/video/show/${videoPage.video.id}?reencode=true" />"><spring:message code="video.re-encode" /></a>
		</div>
	</div>

	<div id="jp_container_1" class="jp-video jp-video-360p">
		<div class="jp-type-single">
			<div id="jquery_jplayer_1" class="jp-jplayer"></div>
			<div class="jp-gui">
				<div class="jp-video-play">
					<a href="javascript:;" class="jp-video-play-icon" tabindex="1">play</a>
				</div>
				<div class="jp-interface">
					<div class="jp-progress">
						<div class="jp-seek-bar">
							<div class="jp-play-bar"></div>
						</div>
					</div>
					<div class="jp-current-time"></div>
					<div class="jp-duration"></div>
					<div class="jp-title">
						<ul>
							<li>${videoPage.video.displayTitle}</li>
						</ul>
					</div>
					<div class="jp-controls-holder">
						<ul class="jp-controls">
							<li><a href="javascript:;" class="jp-play" tabindex="1">play</a></li>
							<li><a href="javascript:;" class="jp-pause" tabindex="1">pause</a></li>
							<li><a href="javascript:;" class="jp-stop" tabindex="1">stop</a></li>
							<li><a href="javascript:;" class="jp-mute" tabindex="1" title="mute">mute</a></li>
							<li><a href="javascript:;" class="jp-unmute" tabindex="1" title="unmute">unmute</a></li>
							<li><a href="javascript:;" class="jp-volume-max" tabindex="1" title="max volume">max volume</a></li>
						</ul>
						<div class="jp-volume-bar">
							<div class="jp-volume-bar-value"></div>
						</div>

						<ul class="jp-toggles">
							<li><a href="javascript:;" class="jp-full-screen" tabindex="1" title="full screen">full screen</a></li>
							<li><a href="javascript:;" class="jp-restore-screen" tabindex="1" title="restore screen">restore screen</a></li>
							<li><a href="javascript:;" class="jp-repeat" tabindex="1" title="repeat">repeat</a></li>
							<li><a href="javascript:;" class="jp-repeat-off" tabindex="1" title="repeat off">repeat off</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="jp-no-solution">
				<span>Update Required</span> To play the media you will need to either update your browser to a recent version or update your <a
					href="http://get.adobe.com/flashplayer/" target="_blank">Flash plugin</a>.
			</div>
		</div>
	</div>


</div>