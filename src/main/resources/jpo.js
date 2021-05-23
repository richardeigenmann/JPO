function changetext(newtext){
    element=document.getElementById("descriptions");
    element.innerHTML=newtext;
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

    //new google.maps.Marker({
    //    position: latLng,
    //    map: map,
    //    draggable: false
    //});
};