function changetext(newtext){
    if (document.all || document.getElementById){
        element=document.getElementById ? document.getElementById("descriptions") : document.all.descriptions;
        element.innerHTML=newtext;
    }
    else if (document.layers){
        document.d1.document.d2.document.write(newtext);
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
