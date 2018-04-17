<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>
<c:set var="mediaEncoding" value="${videoPage.video.bestMediaEncoding}" />

<script type="text/javascript">
    $(document).ready(function() {
        
         $("div.breadcrumbs span:last-child").editable("<c:url value="/app/restful/media/save-media-name" />", {
             tooltip: "<spring:message code="action.click.edit" />",             
             submitdata : {id: ${videoPage.video.id}},
             data: function(value, settings) {
                 return $.trim(value);
                 
             }
         });

        $("#re-encode").click(function() {
            $.post("<c:url value="/app/restful/encode/video" />", {
                id: ${videoPage.video.id}
            }).done(function(data) {
                mashupMedia.showMessage(data);
            });
        });

    });
</script>

<br />

<div class="control-menu"
	id="album-id-<c:out value="${albumPage.album.id}" />">

	<a href="javascript:;" id="re-encode"
		title="<spring:message code="action.re-encode.tip" />"><spring:message
			code="action.re-encode" /></a>
</div>

<video controls id="video-player">
	<source
		src="<c:url value="/app/streaming/media/${videoPage.video.id}/${mediaEncoding.mediaContentType.jPlayerContentType}" />"
		type="${mediaEncoding.mediaContentType.mimeContentType}" />
</video>

