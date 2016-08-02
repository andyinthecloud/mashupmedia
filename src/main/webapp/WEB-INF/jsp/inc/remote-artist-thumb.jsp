<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {        
   		$.getJSON("<c:url value="/app/ajax/music/artist/remote/${artistId}" />", function( data ) {
   			displayRemoteArtistInformation(data);			
   		});            
    });

    function displayRemoteArtistInformation(data) {
        if (data.error) { return; }
        
        var thumb = getArtistImage(data.remoteImages);
        $("#artist-id-${artistId} .thumb").html(thumb);

    }
    
    function getArtistImage(remoteImages) {
        var artistImage = "";
        if (!remoteImages.length || remoteImages.length == 0) {
            return artistImage;
        }
        
        var firstArtistImage = remoteImages[0];
        artistImage = "<img alt=\"image\" src=\"" + mashupMedia.contextUrl + "/" + firstArtistImage.thumbUrl + "\"/>";
        return artistImage;
    }
    
</script>


