var mymap = L.map('mapid').setView([51.505, -0.09], 3);

var mapLayer = L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
    maxZoom: 20,
    id: 'mapbox/streets-v11',
    tileSize: 512,
    zoomOffset: -1,
    accessToken: 'pk.eyJ1IjoicGl5dXNoYjkiLCJhIjoiY2twNzRhNDV0MmFsODJ2bXc3MDc2OHJhNyJ9.YUES1D6oFw462dAE8Vp-Zg'
}).addTo(mymap);

let srcLat = srcLng = undefined; //lat,lng of starting point
let destLat = destLng = undefined;//lat,lng of ending point
let sourceChosen = false, destinationChosen = false;
var fromMarker, toMarker;
var pathMarkers, line;
var selectionPopup = L.popup();

function reset() {
    sourceChosen = false;
    destinationChosen = false;
    srcLat = srcLng = undefined;
    destLat = destLng = undefined;
    $("#route_from").val("From");
    $("#route_to").val("To");
    $("#distance").val("Distance in meters");
    mymap.closePopup();
    if(fromMarker)
        mymap.removeLayer(fromMarker);
    if(toMarker)
        mymap.removeLayer(toMarker);
    if(line)
        mymap.removeLayer(line);
}

function loadButtons(e) {
    var buttonHTML = ``;
    if (!sourceChosen || !destinationChosen) {
        buttonHTML = buttonHTML + `\n<input type="button" class="btn" onclick="setPoint('s', ${e.latlng.lat} , ${e.latlng.lng})" value="Select as Source point"/>`;
        buttonHTML = buttonHTML + `\n<input type="button" class="btn" onclick="setPoint('d', ${e.latlng.lat} , ${e.latlng.lng})" value="Select as Destination point"/>`;
        buttonHTML = buttonHTML + `\n<input type="button" class="btn" onclick="reset()" value="Reset Data"/>`;
    }
    else if(!destinationChosen) {
        buttonHTML = buttonHTML + `\n<input type="button" class="btn" onclick="setPoint('s', ${e.latlng.lat} , ${e.latlng.lng})" value="Select as Source point"/>`;
        buttonHTML = buttonHTML + `\n<input type="button" class="btn" onclick="setPoint('d', ${e.latlng.lat} , ${e.latlng.lng})" value="Select as Destination point"/>`;
        buttonHTML = buttonHTML + `\n<input type="button" class="btn" onclick="reset()" value="Reset Data"/>`
    }
    else {
        buttonHTML = buttonHTML + `\n<input type="button" class ="btn" onclick="dist()" value="Calculate Distance"/>` + `\n<input type="button" class="btn" onclick="reset()" value="Reset Data"/>`;
    }
    return buttonHTML;
}


function onMapClick(e) {
    selectionPopup
        .setLatLng(e.latlng)
        .setContent(
            `<div>
            <div>You clicked at ${e.latlng.toString()}</div>
            ${loadButtons(e)}
        </div>`);
    mymap.openPopup(selectionPopup);
}

mymap.on('click', onMapClick);

function setPoint(point, lat, lng) {
    if (point === 's') {
        $("#route_from").val(lat.toString() + "," + lng.toString());
        srcLat = lat;
        srcLng = lng;
        sourceChosen = true;
        fromMarker = L.marker([lat, lng]).bindPopup("Starting point").addTo(mymap);
    }
    else if (point === 'd') {
        $("#route_to").val(lat.toString() + "," + lng.toString());
        destLat = lat;
        destLng = lng;
        destinationChosen = true;
        toMarker = L.marker([lat, lng]).bindPopup("Destination point").addTo(mymap);
    }
    mymap.closePopup();
}

function initFailure(data, status) {
    alert("Error while initializing data.\n!!Please refresh the page!!")
}

$.ajax({
    type: 'GET',
    url: `/navigation/init`,
    contentType: 'application/json',
    error: initFailure

});

function successCallback(data, status) {
    $("#distance").val(data.distance+" meters");
    if(data.distance == -1) {
        alert("Please recheck the points you have selected.\nThere is no path connecting the two.\nPerhaps you selected one point on land.\nYou can reset the data and try again.");
    }
    else {

        var myLines = [{
            "type": "LineString",
            "coordinates": data.points
        }];
        
        var myStyle = {
            "color": "#ff7800",
            "weight": 2,
            "opacity": 0.65
        };
        
        line = L.geoJSON(myLines, {
            style: myStyle
        }).addTo(mymap);
        
    }
}

function errorCallback(data, status) {
    alert("Error calculating data");
}


function dist() {

    if (!sourceChosen || !destinationChosen) {
        alert("Please select source and destination both");
        return false;
    }

    $("#distance").val("loading...");
    
    $.ajax({
        type: 'GET',
        url: `/navigation/distance`,
        data: `sourceLat=${srcLat}&sourceLng=${srcLng}&destLat=${destLat}&destLng=${destLng}`,

        success: successCallback,
        error: errorCallback
    });
}
