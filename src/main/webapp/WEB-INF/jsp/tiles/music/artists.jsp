<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {
        // Unbind declared event handlers
        $("div.dynamic-content").off("click", "ul.index-letters a");

        window.scrollTo(0, 0);
        showFooterTabs("music-artists");

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
        
        $("div.dynamic-content div.artists").off("mouseover", "div.artist").on("mouseover", "div.artist", function() {
            $(this).addClass("highlight");
        });

        
        $("div.dynamic-content div.artists").off("mouseout", "div.artist").on("mouseout", "div.artist", function() {
            $(this).removeClass("highlight");
        });
        
       	$("div.dynamic-content div.artists").off("click", "div.artist div.artist-control a").on("click", "div.artist div.artist-control a", function() {
       	 	if ($(this).hasClass("play")) {
       	 		playArtist(this);    
       	 	} else if ($(this).hasClass("add")) {
       	 		appendArtist(this);    
       	 	} 
        	
		}); 
       	
       	
        function playArtist(element) {
            var artistId = $(element).closest("div.artist-control").attr("id");
            artistId = parseId(artistId, "artist-id");
            mashupMedia.playArtist(artistId);
        }
        
        function appendArtist(element) {
            var artistId = $(element).closest("div.artist-control").attr("id");
            artistId = parseId(artistId, "artist-id");
            mashupMedia.appendAlbum(artistId);
        }       	
        

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

<div class="artists">
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


		<div class="artist" id="${rowIndex}">


			<a rel="internal"
				title="<spring:message code="music.title" /> - ${artist.name}"
				href="<c:url value="/music/artist/${artist.id}"/>"> <img class="artist-art"
				src="<c:url value="/remote/music/artist/image/${artist.id} "/>"
				title="${artist.name}" alt="${artist.name}" />
			</a>


			<div class="artist-title">
				<div class="artist-name">
					<a rel="internal"
						title="<spring:message code="music.title" /> - ${artist.name}"
						href="<c:url value="/music/artist/${artist.id}" />">${artist.name}</a>
				</div>
			</div>


			<div class="artist-control" id="artist-id-${artist.id}">
				<a class="play" href="javascript:;"
					title="<spring:message code="action.play" />"><img
					alt="<spring:message code="action.play"/>"
					title="<spring:message code="action.play"/>"
					src="<c:url value="${themePath}/images/controls/play.png"/>" /></a>
				<c:if test="${isPlaylistOwner}">
					<a class="add" href="javascript:;"
						title="<spring:message code="action.add" />"><img
						alt="<spring:message code="action.add"/>"
						title="<spring:message code="action.add"/>"
						src="<c:url value="${themePath}/images/controls/add.png"/>" /></a>
				</c:if>
			</div>




		</div>

	</c:forEach>
</div>


