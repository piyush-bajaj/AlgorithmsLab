# AlgorithmsLab

Pre-requisites:
java 1.8+ must be installed on the system and java_home must be set to run java command
docker could be present on the machine or an online lab can also be used. eg https://labs.play-with-docker.com/


Instructions for execution:
Checkout the repository on the system.

Open the Grid Generation folder and run the provided jar file with the following command:
    java -jar Navigation.jar <pbf_file_name.pbf> <grid_rows(lat_lines)> <grid_columns(lng_lines)>

eg: java -jar Navigation.jar antarctica-latest.osm.pbf 200 500

This will generate a file NodesEdges.txt

Copy and Paste this file in the folder Server/Spring (Replace NodesEdges.txt)

Next navigate to the folder Server and the following commands in the docker

    docker-compose build

    docker-compose up -d

wait for 2 mins for the server to start and then open the url of host machine (localhost or online docker)

The binding port is 8080

For online docker, it provides a url with its mapping directly, simply access that, for docker on system, try the url http://localhost:8080 or http://127.0.0.1:8080

This will open up the application, that will show the map.

The search fields are only for displaying info and are not editable.

Click on any point on the map, and click the option "Select as Source point".

Now click on some other point on the map and click the option "Select as Destination point" 

Now you can click on "Go" option provided to calculate the distance which will updated and printed in the "Distance in meters" field.

You can also reset the selected points and change the source and destination points.

To shutdown docker, run the below command
    docker-compose down
