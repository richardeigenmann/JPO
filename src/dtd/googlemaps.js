( function() {
    window.onload = function () {
        /* Read the coordinates off the URL */
        var lat = 0;
        var lng = 0;
        var hashcode;
        var locationSearch = location.search.substr(1).split(";");
        for (var i=0; i<locationSearch.length;i++)
        {
            var y = locationSearch[i].split("=");
            if ( y[0] == "lat" ) {
                lat = y[1];
            } else if ( y[0] == "lng" ) {
                lng = y[1];
            } else if (y[0] == "hashcode" ) {
                hashcode = y[1];
            }
        }
        document.getElementById("hashcode").value=hashcode;
        var latLng= new google.maps.LatLng(lat, lng);
        updatePsn(latLng);

        var mapDiv = document.getElementById("map");
        var options = {
            center: latLng,
            zoom: 4,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        var map = new google.maps.Map( mapDiv, options );

        var markerLatLng= new google.maps.LatLng(lat, lng);
        
        var marker = new google.maps.Marker({
            position: markerLatLng,
            map: map,
            draggable: true
        })

        google.maps.event.addListener(marker, 'position_changed', function() {
            updatePsn(marker.getPosition() );
        });
    }

    function updatePsn( psn ) {
        document.getElementById("latitude").value = psn.lat();
        document.getElementById("longitude").value = psn.lng();
    }
})();
