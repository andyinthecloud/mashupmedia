<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<%
 	pageContext.setAttribute("newLine", "\n");
%>


<script type="text/javascript">
	$(document).ready(function() {
		$("#meta-section a").click(function() {
			$("#meta-section p.hide").toggle("slow", function() {
				// Animation complete.
			});
		});
	});
</script>

<div class="sub-panel">

	<c:set var="photo" value="${photoPage.photo }" />

	<h1>${photo.album.name} / ${photo.displayTitle}</h1>

	<table class="photo-container">
		<tr>
			<td class="photo-previous"><c:if
					test="${not empty photoPage.previousPhoto.id}">
					<a
						href="<c:url value="/app/photo/show/${photoPage.previousPhoto.id}" />"
						class="arrow image-previous"></a>
				</c:if></td>
			<td class="photo"><img class="original-photo"
				alt="${photo.displayTitle}" title="${photo.displayTitle}"
				src="<c:url value="/app/photo/original/${photo.id}" />" /></td>
			<td class="photo-next"><c:if
					test="${not empty photoPage.nextPhoto.id}">
					<a
						href="<c:url value="/app/photo/show/${photoPage.nextPhoto.id}" />"
						class="arrow image-next"></a>
				</c:if></td>
		</tr>
	</table>

	<ul class="items">
		<li><a href="<c:url value="/app/photo/original/${photo.id}" />"><spring:message
					code="photo.original" /></a></li>
					
		<li id="meta-section"><a href="javascript:;"><spring:message
					code="photo.meta" /></a> 
			<p class="hide">${fn:replace(photo.metadata, newLine, "<br />")}</p>
		</li>
	</ul>

</div>