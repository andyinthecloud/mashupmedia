<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>
<c:set var="mediaEncoding" value="${videoPage.video.bestMediaEncoding}" />

<script type="text/javascript">
    $(document).ready(function() {
        

        $("h1.edit").editable("<c:url value="/app/restful/media/save-media-name" />", {
            tooltip: "<spring:message code="action.click.edit" />"
        });
                

    });
</script>



<h1 class="edit" id="${videoPage.video.id}">${videoPage.video.displayTitle}</h1>




 <video width="320" height="240" controls>
  
  <source src="<c:url value="/app/streaming/media/${videoPage.video.id}?mediaContentType=${mediaEncoding.mediaContentType.jPlayerContentType}" />" type="${mediaEncoding.mediaContentType.mimeContentType}"/>
  
	Your browser does not support the video tag.
</video> 

