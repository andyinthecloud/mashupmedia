<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>
<c:set var="mediaEncoding" value="${videoPage.video.bestMediaEncoding}" />

<script type="text/javascript">
    $(document).ready(function() {

        $("h1.edit").editable("<c:url value="/app/restful/media/save-media-name" />", {
            tooltip: "<spring:message code="action.click.edit" />"
        });

    });
</script>

<jsp:include page="/WEB-INF/jsp/inc/remote-video-info-js.jsp" />


<h1 class="edit" id="${videoPage.video.id}">${videoPage.video.displayTitle}</h1>

<div id="remote">
	<a class="arrow-show-hide" href="javascript:void(0)"> <img
		src="<c:url value="/images/arrow-down.png" />" /></a>
	<div class="profile">${videoPage.video.summary}</div>
	<div class="images"></div>

	<div class="disclaimer">
		<spring:message code="music.artists.remote" />
		<a href="http://www.last.fm" target="_blank" title=""><img
			title="last.fm" src="<c:url value="/images/lastfm.png" />" /></a>. <a
			class="incorrect" href="javascript:;"><spring:message
				code="music.artists.remote.correct" /></a> | <a
			href="<c:url value="/app/video/show/${videoPage.video.id}?reencode=true" />"><spring:message
				code="video.re-encode" /></a>
	</div>
</div>




