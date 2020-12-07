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


window.initMap = function() {
    const mapDiv = document.getElementById("map");
    const latLng= new google.maps.LatLng(lat, lng);
    const options = {
        center: latLng,
        zoom: 4,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    const map = new google.maps.Map( mapDiv, options );

    new google.maps.Marker({
        position: latLng,
        map: map,
        draggable: false
    });
};