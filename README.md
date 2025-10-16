# LogParser
Log File Analyser parses system logs, tracks user activity, detects suspicious logins, and exports reports via REST API.

## Accessing the hosted application
If you don't feel like running the docker container locally I have made it as easy as possible to see a running version 
of the application.

Please just visit the following:
https://logparser.onrender.com/swagger-ui/index.html

NB: If the link is taking a while to load please be patient. To use the free hosting there is down scaling taking place
when the deployed service becomes inactive and will need to bring the deployment back online before rendering the 
swagger to the client.

## Running Locally using docker/podman

If you would like to run the project locally please pull the public image from:

```pdoman pull docker.io/k33w3r/logparser:latest```

Then simply run from the <project-root-directory>/docker:

```podman-compose up```