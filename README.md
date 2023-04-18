# titlebot

## Prerequisites
- Must have Docker running on your maching
- Must have sbt
- Must have npm

## Running titlebot
- Open a terminal, and first navigate to titlebotserver.
    - Run `docker compose up` to initialize mongo.
    - In a separate terminal window/tab in the same directory, run `sbt run` to intitialize the server.
- After the server has been initialized, open a terminal at titlebotfrontend.
    - Run `npm install` then `npm start` to initialize the web server.
    - The page will be available at `localhost:3000`