drop database if exists airline;
create database airline;
use airline;

# Table Schema
# 5 relations with 15 attributes minimum
# Key constraints in all relations

drop table if exists Passenger;
create table Passenger
(
 uID int primary key AUTO_INCREMENT,
 name varchar(30),
 numBags int,
 totalBagWeight float
);

drop table if exists Flights;
create table Flights
(
 fID int primary key AUTO_INCREMENT,
 planeID int references Planes(planeID),
 startID int references Location(locationID),
 destID int references Location(locationID),
 time timestamp
);

drop table if exists Planes;
create table Planes
(
 planeID int primary key AUTO_INCREMENT,
 numPassengers int,
 numEcon int,
 numBusiness int,
 numFirst int,
 currentFlight int references Flights(fID)
);

drop table if exists Location;
create table Location
(
 locationID int primary key,
 name varchar(30)
);

drop table if exists Reservations;
create table Reservations
(
 rID int primary key AUTO_INCREMENT,
 planeID int references Planes(planeID),
 uID int references Passengers(uID),
 econPass boolean,
 businessPass boolean,
 firstPass boolean,
 ticketCost float
);

# Constraints



# Triggers


# Functional Requirements
# Have one of each: correlated subquery, group by and having, outer join, set operation
# 3 queries need to involve multiple relations

# Reserve a flight
