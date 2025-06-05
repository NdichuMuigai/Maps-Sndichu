import React, { useRef, useEffect } from "react";
import mapboxgl from "mapbox-gl";

// Assuming the access token is set in the environment variables
mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_ACCESS_TOKEN;

// console.log(actualData);
const queryParams = new URLSearchParams(location.search);
const keyword = queryParams.get("keyword");
console.log(keyword);
const minLat = queryParams.get("minLat");
const maxLat = queryParams.get("maxLat");
const minLng = queryParams.get("minLng");
const maxLng = queryParams.get("maxLng");

const resp = await fetch("http://localhost:8080/loadjson?file=map.geojson"); // Replace with the path to your redlining data
if (!resp.ok) {
  console.error("Failed to load geojson");
  
}
const respJson = await resp.json();
console.log(respJson);
if ((await respJson.result) !== "success") {
  console.log(resp.json());
  console.error("Failed to load geojson");
}
// Add the redlining data source
// send all queries to filter json, and props as they exist, just forward all props
const stringQueries = `?${
  keyword ? `keyword=${encodeURIComponent(keyword)}` : ""
}${minLat ? `&minLat=${encodeURIComponent(minLat)}` : ""}${
  minLng ? `&minLng=${encodeURIComponent(minLng)}` : ""
}${maxLat ? `&maxLat=${encodeURIComponent(maxLat)}` : ""}${
  maxLng ? `&maxLng=${encodeURIComponent(maxLng)}` : ""
}`;

const data = await fetch("http://localhost:8080/filterjson" + stringQueries);
const json = await data.json();
console.log("json", json);
const actualData = json.data;

console.log(actualData);
const Map = () => {
  const mapContainerRef = useRef(null);

  useEffect(() => {
    const map = new mapboxgl.Map({
      container: mapContainerRef.current, // container ID matches the ref on the div
      style: "mapbox://styles/mapbox/streets-v11", // map style
      center: [-74.5, 40], // starting position
      zoom: 9, // starting zoom
    });

    // Load redlining data once the map has finished loading
    map.on("load", () => {
      console.log(actualData);
      map.addSource("redlining", {
        type: "geojson",
        data: actualData, // Replace with the path to your redlining data
      });

      // Add a layer to visualize the redlining data
      map.addLayer({
        id: "redlining",
        type: "fill",
        source: "redlining",
        layout: {},
        paint: {
          "fill-color": "#ff0000", // Use a color of your choice
          "fill-opacity": 0.5,
        },
      });

      // Add click event handler
      map.on("click", "redlining", e => {
        const features = map.queryRenderedFeatures(e.point, {
          layers: ["redlining"],
        });

        if (features.length > 0) {
          const feature = features[0];
          console.log(features);
          // Assuming 'state', 'city', and 'name' are keys in your data
          const { state, city, name, neighborhood_id } =
            feature.properties;
          // const parsed = JSON.parse(area_description_data);
          // console.log("value", parsed);
          const properties = { name: name, city: city, state: state, id: neighborhood_id };
          // loop through possible properties, and if they exist, add html for them and their value
          // TODO: create an interface for these types
          const html = Object.keys(properties).filter(key => properties[key])
            .map(
              key =>
                `<div><strong>${key}</strong>: ${properties[key]}</div>`
            )
            .join("");
          new mapboxgl.Popup().setLngLat(e.lngLat).setHTML(html).addTo(map);
        }
      });
    });

    // Clean up on unmount
    return () => map.remove();
  }, [actualData]);

  return (
    <div
      id="map-container"
      ref={mapContainerRef}
      style={{
        height: "100vh",
        width: "100vw",
        position: "fixed",
        top: "0",
        left: "0",
      }}
    >
      {/* The map will be inserted here by Mapbox GL JS */}
    </div>
  );
};

export default Map;
