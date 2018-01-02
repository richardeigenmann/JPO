function changetext(whichcontent){
    if (document.all||document.getElementById){
        cross_el=document.getElementById? document.getElementById("descriptions"):document.all.descriptions;
        cross_el.innerHTML=whichcontent;
    }
    else if (document.layers){
        document.d1.document.d2.document.write(whichcontent);
        document.d1.document.close();
    }
}


( function() {
    window.onload = function () {
        var latLng= new google.maps.LatLng(lat, lng);

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
            draggable: false
        });
    };

})();
