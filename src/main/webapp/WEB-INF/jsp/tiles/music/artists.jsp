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

        $(window).scroll(function() {

            if ($(this).scrollTop() >= 100) {

                $("ul.index-letters").addClass("sticky");
            } else {
                $("ul.index-letters").removeClass("sticky");
            }
        });

    });
</script>


<h1>
	<spring:message code="music.artists.title" />
</h1>


<c:set var="nonAlphabeticalId" value="non-alphabetical-id" />

<ul class="index-letters">
	<c:forEach items="${artistsPage.artistIndexLetters}" var="letter">
		<c:set var="urlLetter" value="${letter}" />
		<c:if test="${urlLetter eq '#'}">
			<c:set var="urlLetter" value="${nonAlphabeticalId}" />
		</c:if>

		<li><a href="#index-letter-${urlLetter}">${letter}</a></li>
	</c:forEach>
</ul>

<ul class="items">
	<c:set var="rowIndex" value="" />
	<c:set var="indexLetter" value="" />
	<c:forEach items="${artistsPage.artists}" var="artist">
		<c:choose>
			<c:when test="${artist.indexLetter != indexLetter}">
				<c:set var="rowIndex" value="index-letter-${artist.indexLetter}" />
				<c:if test="${artist.indexLetter eq '#'}">
					<c:set var="rowIndex" value="index-letter-${nonAlphabeticalId}" />
				</c:if>


				<c:set var="indexLetter" value="${artist.indexLetter}" />
			</c:when>
			<c:otherwise>
				<c:set var="rowIndex" value="" />
			</c:otherwise>
		</c:choose>

		<li id="${rowIndex}"><a rel="internal"
			href="<c:url value="/app/music/artist/${artist.id}"/>"><c:out
					value="${artist.name}" /></a></li>
	</c:forEach>
</ul>

