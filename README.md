# Crowdsourcing Freshwater Backend

----
## Overview
This is a barebones Spring Boot application that serves a basic, unsecured REST API.

For database support, we used H2 with on-disk persistence for development and with in-memory persistence for testing. This was to make it trivial for anyone to run a local copy of the server without needing to set up some other database. For a real application, it would probably be better to swap the configuration for development to support Postgres or MySQL and leave the testing database as H2.

Because of the very short timeframe and the "extra-credit" nature of this project, it does some things that would never be done in a real application which are detailed below. The most glaring example is the "/login" endpoint; in a real application, this would return a JWT or OAuth token that would be used for authentication everywhere else. This token would then be used for authentication for most of the other endpoints.

Also, passwords are stored as plain text to make initial testing easier; in a real application, they would be salted and hashed via Spring's BCryptPasswordEncoder (or in the future, whatever encoder is the standard).

Finally, there is a "/reset" end point that clears the database and repopulates it. There are a few better solutions that would depend on the database and design considerations (since we initialize with randomized data).

----
## Motivation
"Our project this semester supports the Georgia Tech Serve/Learn/Sustain program. More details on this program are [here](￼http://serve-learn-sustain.gatech.edu/welcome). It is loosely based on the NASA challenge [here](https://2015.spaceappschallenge.org/challenge/clean-water-mapping/).

Our application will allow water reporting to crowd sourced. People can report on locations where water is available. Users of the application can find the nearest water source or report on a new water source. Workers with test kits will also be able to report on contaminant levels. The application will provide historical graphs to show variations in water quality over time for a specific location.

A normal application like this would be networked and have a database backend. For this class, those skills are not pre-requisites, so our application will be simulated multi-user. We will assume all the data will fit into memory, so that you may store data in an appropriate basic data structure. We will persist (save the data) to disk. Making an actual networked application with a database is extra credit." -Assignment Description

This project is for that networked extra credit.
