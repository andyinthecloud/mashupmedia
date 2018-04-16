<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>
<c:set var="mediaEncoding" value="${videoPage.video.bestMediaEncoding}" />

<script type="text/javascript">
    $(document).ready(function() {
        
         $("div.breadcrumbs span:last-child").editable("<c:url value="/app/restful/media/save-media-name" />", {
             tooltip: "<spring:message code="action.click.edit" />"
         });

        /*

        $("h1.edit").editable("<c:url value="/app/restful/media/save-media-name" />", {
            tooltip: "<spring:message code="action.click.edit" />"
        });
        */

        $("#re-encode").click(function() {
            $.post("<c:url value="/app/restful/encode/video" />", {
                id: ${videoPage.video.id}
            }).done(function(data) {
                mashupMedia.showMessage(data);
            });
        });

    });
</script>

<h1 class="edit" id="${videoPage.video.id}">${videoPage.video.displayTitle}</h1>

<div class="control-menu"
	id="album-id-<c:out value="${albumPage.album.id}" />">

	<a href="javascript:;" id="re-encode"
		title="<spring:message code="action.re-encode.tip" />"><spring:message
			code="action.re-encode" /></a>
</div>

<video width="320" height="240" controls>
	<source
		src="<c:url value="/app/streaming/media/${videoPage.video.id}?mediaContentType=${mediaEncoding.mediaContentType.jPlayerContentType}" />"
		type="${mediaEncoding.mediaContentType.mimeContentType}" />
</video>

