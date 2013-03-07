<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {
	$("#play-all").click(function() {
	    var artistId = $(this).closest("ul").attr("id");
	    artistId = parseId(artistId, "artist-id");
	    mashupMedia.playArtist(artistId);
	});

	$("#add-all").click(function() {
	    var artistId = $(this).closest("ul").attr("id");
	    artistId = parseId(artistId, "artist-id");
	    mashupMedia.appendArtist(artistId);
	});

	$("ul.items ul.control-menu li.play-album a").click(function() {
	    var albumId = $(this).closest("li.item").attr("id");
	    albumId = parseId(albumId, "album-id");
	    mashupMedia.playAlbum(albumId);
	});

	$("ul.items ul.control-menu li.append-album a").click(function() {
	    var albumId = $(this).closest("li.item").attr("id");
	    albumId = parseId(albumId, "album-id");
	    mashupMedia.appendAlbum(albumId);
	});

	$("#albums li a").click(function() {
	    fireRelLink(this);
	});

	$("#albums li").hover(function() {
	    $(this).find("a.album-cover").addClass("highlight");
	}, function() {
	    $(this).find("a.album-cover").removeClass("highlight");
	});

	$("div.information div.images .fancybox").fancybox();

	$("div.information div.discogs a.incorrect").click(function() {
	    $("#discogs-dialog").dialog();
	    $("#discogs-dialog input[type=text]").blur();
	});
	

	var artistNameLabel = "<spring:message code="music.artists.discogs.search" />"; 
	    
	
	$("#discogs-dialog input[type=text]").blur(function() {
	    var artistName = $.trim($(this).val());
	    if (artistName.length == 0) {
			$(this).val(artistNameLabel);
	    }
	});

	
	$("#discogs-dialog input[type=text]").focus(function() {
	    var artistName = $.trim($(this).val());
	    if (artistName == artistNameLabel) {
			$(this).val("");
	    }
	});
	

	$("#discogs-dialog input[type=button]").click(function() {
	    var searchArtist = $("#discogs-dialog input[type=text]").val();
	    if (searchArtist == artistNameLabel) {
			return;
	    }	    
	    
	    	    
	    
	    $.post("<c:url value="/app/ajax/discogs/search-artist" />", { name: searchArtist}).done(function(data) {
			var artistsHtml = "";
	   		$.each(data, function(index) {
	   		 	artistsHtml += "<li><a id=\"search-results-discogs-id-" + data[index].id + "\" href=\"javascript:;\">" + data[index].name + "</a></li>"
			});
	   		$("#discogs-dialog ul.search-results").html(artistsHtml);
	    });
	});

	$("#discogs-dialog ul.search-results li a").live("click", function(event){
	    alert("click");
	});  
	    
	$("#discogs-dialog ul.search-results li a").on("click", function(event) {
		var discogsId = $(this).attr("id");
		discogsId = parseId(discogsId, "search-results-discogs-id");		
		alert(discogsId);

	    var artistId = $(this).closest("ul").attr("id");
	    artistId = parseId(albumId, "search-results-artist-id");
	    $.post("<c:url value="/app/ajax/discogs/save-artist" />", { discogsId: discogsId, artistId: artistId}).done(function(data) {
		    $.get("<c:url value="/app/ajax/discogs/discogs-artist-id/" />/" + discogsId).done(function(data) {
				logger.console(data);
		    });	    		    		    	
		});	    	
		
		
	});

	
	});
</script>

<div id="discogs-dialog" class="dialog" title="Search Discogs for artist information">
	<p>
		<input type="text" name="name" class="search-field" value="<spring:message code="music.artists.discogs.search" />" /><input type="button" value="Search" />
	<ul class="search-results" id="search-results-artist-id-${artistPage.artist.id}">


	</ul>


	</p>
</div>


<h1>${artistPage.artist.name}</h1>
<ul class="control-menu" id="artist-id-<c:out value="${artistPage.artist.id}" />">
	<li class="first"><a href="javascript:;" id="play-all"><spring:message code="action.play-all" /></a></li>
	<c:if test="${isPlaylistOwner}">
		<li><a href="javascript:;" id="add-all"><spring:message code="action.add-all" /></a></li>
	</c:if>
</ul>

<div class="information">
	<div class="profile">
		<c:out value="${artistPage.remoteMediaMeta.profile}" escapeXml="false" />
	</div>

	<div class="images">
		<c:forEach items="${artistPage.remoteMediaMeta.remoteImages}" var="remoteImage">
			<a class="fancybox" rel="artist-images" href="<c:url value="${remoteImage.imageUrl}" />"><img src="<c:url value="${remoteImage.thumbUrl}" />" /></a>
		</c:forEach>
	</div>

	<div class="discogs">
		<spring:message code="music.artists.discogs" />
		<img src="<c:url value="/images/discogs.png" />" />. <a class="incorrect" href="javascript:;"><spring:message code="music.artists.discogs.correct" /></a>

	</div>
</div>


<ul id="albums" class="items">
	<c:forEach items="${artistPage.artist.albums}" var="album">
		<li id="album-id-${album.id}" class="item"><a class="album-cover" href="javascript:;" rel="address:/address-load-album-${album.id}"> <img src="<c:url value="/app/music/album-art-thumbnail/${album.id}" />" title="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />"
				alt="<c:out value="${album.artist.name}" /> - <c:out value="${album.name}" />" />
		</a>
			<div class="album">
				<a href="javascript:;" rel="address:/address-load-album-${album.id}"><c:out value="${album.name}" /></a>
			</div>
			<div class="artist">
				<a href="javascript:;" rel="address:/address-artist-${album.artist.id}"><c:out value="${album.artist.name}" /></a>
			</div>

			<ul class="control-menu">
				<li class="first play-album"><a href="javascript:;"><spring:message code="action.play-all" /></a></li>
				<li class="append-album"><a href="javascript:;"><spring:message code="action.add-all" /></a></li>
			</ul></li>
	</c:forEach>
</ul>
