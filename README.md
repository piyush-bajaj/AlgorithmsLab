# AlgorithmsLab
Instructions

Open the Grid Generation folder and run the provided jar file

Command:  java -jar Shipping-Navigation-Verbose.jar antarctica.

pbf 200 500 > outpur.txt

This will generate a file NodesEdges.txt

Paste this file in the folder Server/Spring

Next run the below commands in docker

navigate to the folder Server

docker-compose build

docker-compose up -d

wait for 2 mins for the server to start and then open the url of 

host machine (localhost or online docker)

The binding port is 8080

this will open up the map

Select any point chose as source or destination or reset.

Afetr selecting points click on go

The distance between the two points is shown

to shutdown docker, run the below command

docker-compose down