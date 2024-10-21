function initMap() {
    const map = new google.maps.Map(document.getElementById('map'), {
        center: {
            lat: 17.385044,
            lng: 78.486671
        },
        zoom: 10,
    });

    let addr;
    const geocoder = new google.maps.Geocoder();
    const service = new google.maps.DistanceMatrixService();

    // Make a GET request to retrieve coordinates from the Spring Boot API
    fetch('/api/distance-matrix/coordinates', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            // Data now contains the list of origins and destinations
            addr = data.origins;
            const origins = data.origins;
            const destinations = data.destinations;

            // Now, you can use the retrieved data to build your DistanceMatrixRequest
            const request = {
                origins: origins,
                destinations: destinations,
                travelMode: google.maps.TravelMode.DRIVING,
                unitSystem: google.maps.UnitSystem.METRIC,
                avoidHighways: false,
                avoidTolls: false,
            };

            // Assuming you have a service object with a getDistanceMatrix method
            // Adjust this part according to your actual service implementation
            service.getDistanceMatrix(request)
                .then(response => {
                    // Now, you can use the response to make the POST request
                    fetch('/api/distance-matrix/calculate', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            body: JSON.stringify(response),
                        })
                        .then(response => {
                            if (!response.ok) {
                                throw new Error('Network response was not ok');
                            }
                            return response.json();
                        })
                        .then(routeData => {
                            console.log(routeData);
                            const routes = routeData; // This is the Java model attribute
                            console.log(routes);
                            const addresses = addr; // This is the Java model attribute containing the list of addresses
                            console.log(addresses);
                            const colors = ['#E74C3C', '#00008B', '#006400', '#F39C12', '#8E44AD', '#1ABC9C', '#D35400', '#2ECC71', '#27AE60', '#F39C12'];


                            routes.forEach((route, i) => {
                                const directionsService = new google.maps.DirectionsService();
                                const directionsRenderer = new google.maps.DirectionsRenderer({
                                    map: map,
                                    suppressMarkers: true,
                                    polylineOptions: {
                                        strokeColor: colors[i % colors.length], // Use a different color for each route
                                    },
                                });

                                const waypoints = route.slice(1, -1).map(index => {
                                    const address = addresses[index];
                                    return {
                                        location: address,
                                        stopover: true
                                    };
                                });

                                const origin = addresses[route[0]];
                                const destination = addresses[route[route.length - 1]];

                                // Geocode origin address
                                geocoder.geocode({
                                    address: origin
                                }, (originResults, originStatus) => {
                                    if (originStatus === 'OK') {
                                        const originLocation = originResults[0].geometry.location;
                                        const originLatLng = {
                                            lat: originLocation.lat(),
                                            lng: originLocation.lng()
                                        };

                                        // Show marker for origin on the map
                                        new google.maps.Marker({
                                            position: originLatLng,
                                            map: map,
                                            title: 'Origin',
                                            icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png',
                                        });

                                        // Geocode destination address
                                        geocoder.geocode({
                                            address: destination
                                        }, (destResults, destStatus) => {
                                            if (destStatus === 'OK') {
                                                const destLocation = destResults[0].geometry.location;
                                                const destLatLng = {
                                                    lat: destLocation.lat(),
                                                    lng: destLocation.lng()
                                                };

                                                // Show marker for destination on the map
                                                new google.maps.Marker({
                                                    position: destLatLng,
                                                    map: map,
                                                    title: 'Destination',
                                                    icon: 'http://maps.google.com/mapfiles/ms/icons/red-dot.png',
                                                });

                                                // Show markers for waypoints on the map
                                                waypoints.forEach((waypoint, index) => {
                                                    geocoder.geocode({
                                                        address: waypoint.location
                                                    }, (waypointResults, waypointStatus) => {
                                                        if (waypointStatus === 'OK') {
                                                            const waypointLocation = waypointResults[0].geometry.location;
                                                            const waypointLatLng = {
                                                                lat: waypointLocation.lat(),
                                                                lng: waypointLocation.lng()
                                                            };

                                                            new google.maps.Marker({
                                                                position: waypointLatLng,
                                                                map: map,
                                                                title: `Waypoint ${index + 1}`,
                                                                icon: {
                                                                    path: google.maps.SymbolPath.CIRCLE,
                                                                    scale: 10,
                                                                    fillColor: colors[i % colors.length],
                                                                    fillOpacity: 1,
                                                                    strokeColor: '#fff',
                                                                    strokeWeight: 1,
                                                                },
                                                            });
                                                        } else {
                                                            console.error(`Geocode for waypoint was not successful for the following reason: ${waypointStatus}`);
                                                        }
                                                    });
                                                });

                                                // ... (remaining code)

                                            } else {
                                                console.error(`Geocode for destination was not successful for the following reason: ${destStatus}`);
                                            }
                                        });

                                    } else {
                                        console.error(`Geocode for origin was not successful for the following reason: ${originStatus}`);
                                    }
                                });

                                const request = {
                                    origin: origin,
                                    destination: destination,
                                    waypoints: waypoints,
                                    optimizeWaypoints: true,
                                    travelMode: google.maps.TravelMode.DRIVING,
                                };

                                directionsService.route(request, (response, status) => {
                                    if (status === 'OK') {
                                        directionsRenderer.setDirections(response);
                                        const routeLegs = response.routes[0].legs;
                                        routeLegs.forEach(leg => {
                                            console.log('Distance: ' + leg.distance.text);
                                            console.log('Duration: ' + leg.duration.text);
                                        });
                                    } else {
                                        console.error(`Directions request failed due to ${status}`);
                                    }
                                });
                            });
                        })
                        .catch(error => {
                            console.error('Error:', error);
                        });
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

// Call the function when the page is loaded or as needed
window.initMap = initMap;