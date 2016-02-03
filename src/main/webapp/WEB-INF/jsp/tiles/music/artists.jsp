<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
        // Unbind declared event handlers
	    $("div.dynamic-content").off("click", "ul.index-letters a");

	    window.scrollTo(0, 0);
	    showFooterTabs("music", "music-artists");
	    
        $("div.dynamic-content").on("click", "ul.index-letters a", function() {
            var anchor = $(this).attr("href");            
            $('html, body').animate({
                scrollTop: $(anchor).position().top - 20
            }, 500);
            return false;
        });
	    
	});
</script>


<h1>
	<spring:message code="music.artists.title" />
</h1>

<ul class="index-letters">
	<c:forEach items="${artistsPage.artistIndexLetters}" var="letter">
		<li><a href="#index-letter-${letter}"><c:out value="${letter}" /></a></li>
	</c:forEach>
</ul>

<ul class="playlist-items">
	<c:set var="rowIndex" value="" />
	<c:forEach items="${artistsPage.artists}" var="artist">
		<c:choose>
			<c:when test="${artist.indexLetter != indexLetter}">
				<c:set var="rowIndex" value="index-letter-${artist.indexLetter}" />
				<c:set var="indexLetter" value="${artist.indexLetter}" />
			</c:when>
			<c:otherwise>
				<c:set var="rowIndex" value="" />
			</c:otherwise>
		</c:choose>

		<li id="${rowIndex}"><a href="<c:url value="/app/music#address-artist-${artist.id}"/>" rel="address:/address-artist-${artist.id}" id="artist-id-${artist.id}"><c:out value="${artist.name}" /></a></li>
	</c:forEach>
</ul>


