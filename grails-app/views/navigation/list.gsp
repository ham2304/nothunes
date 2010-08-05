
<%@ page import="fr.xebia.nothunes.domain.Track" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title>Navigation</title>
        
        <g:javascript>

		        
			function filterByBand() {
				var bandId = $F('bandSelect');
				<g:remoteFunction action="ajaxFilterTrackByBand" params="'id=' + bandId" onComplete="updateTrackList(e)"/>
				<g:remoteFunction action="ajaxFilterAlbumByBand" params="'id=' + bandId" onComplete="updateAlbums(e)"/>
			}
			
			function filterByAlbum() {
				var bandId = $F('bandSelect');
				var albumId = $F('albumSelect');
				<g:remoteFunction action="ajaxFilterTrackByBand" params="'id=' + albumId + '&band='+escape(bandId)" onComplete="updateTrackList(e)"/>
			}
			
			
			
			
			function updateTrackList(e) {
				$('navTrack').innerHTML = e.responseText;
			}
			
			function updateAlbums(e) {
		// The response comes back as a bunch-o-JSON
		var albums = eval("(" + e.responseText + ")")	// evaluate JSON

		if (albums) {
			var rselect = $('albumSelect')

			// Clear all previous options
			var l = rselect.length

			while (l > 0) {
				l--
				rselect.remove(l)
			}

			var opt = document.createElement('option');
			opt.text = 'All'
			opt.value = ''
			try {
		    	rselect.add(opt, null) // standards compliant; doesn't work in IE
		  	}
			catch(ex) {
		   		rselect.add(opt) // IE only
			}
		  		

			// Rebuild the select
			for (var i=0; i < albums.length; i++) {
				var anAlbum = albums[i]
				var opt = document.createElement('option');
				opt.text = anAlbum.name
				opt.value = anAlbum.id
			  	try {
			    	rselect.add(opt, null) // standards compliant; doesn't work in IE
			  	}
		  		catch(ex) {
		    		rselect.add(opt) // IE only
		  		}
			}
		}
	}
	
        </g:javascript>
    </head>
    <body>
        <div class="nav">
            
        </div>
        <div class="body">
            <h1>Navigation</h1>
            
            <g:if test="${flash.message}">
            	<div class="message">${flash.message}</div>
            </g:if>
            
            <div class="navigation">
            
	            <!-- band selection -->
	            <div id="navBand" class="list">
		            <table>
		            	<thead>
		            		<tr>
		            			<th class="sortable">Bands</th>
		            		</tr>
		            	</thead>
		            </table>
	            	
	            	<g:select id="bandSelect" name="band" class="navigationSelect"
						from="${bandList}"
						noSelection="['':'All']" optionValue="name"
						optionKey="id" size="30" onchange="filterByBand()"/>
	            	
	            </div>
	            
	            <!-- album selection -->
	            <div id="navAlbum" class="list">
	            	<table>
		            	<thead>
		            		<tr>
		            			<th class="sortable">Albums</th>
		            		</tr>
		            	</thead>
		            </table>
	            	<g:select id="albumSelect" name="album" class="navigationSelect"
						from="${albumList}"
						noSelection="['':'All']" optionValue="name"
						optionKey="id" size="30" onchange="filterByAlbum()"/>
	            </div>
	            
	            <div id="navTrack" class="list">
	            	<g:render template="/navigation/trackList"/>
	            </div> 
                
            </div>
        </div>
    </body>
</html>