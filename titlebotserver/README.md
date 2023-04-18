# Titlebot server for Chatmeter interview challenge

To run this service, you must have Docker running on your machine, as well as having sbt installed.

First, run `docker compose up` to initialize mongodb.

Second, run `sbt run` to start the server.

The only route is `localhost/title/[URL]`.