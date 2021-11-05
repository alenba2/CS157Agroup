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
 locationID int primary key AUTO_INCREMENT,
 name varchar(30)
);

drop table if exists Reservations;
create table Reservations
(
 rID int primary key AUTO_INCREMENT,
 fID int references Flights(fID),
 uID int references Passengers(uID),
 econPass boolean,
 businessPass boolean,
 firstPass boolean,
 ticketCost float
);

# Constraints



# Triggers

## If baggage weight is over limit, charge extra on ticket

drop trigger if exists overweightBagCharge;
delimiter //
create trigger overweightBagCharge
after insert on reservation
for each row
begin
	if (select totalBagWeight from passenger where new.uid = passenger.uid) > 50 then
		update reservation set ticketCost = ticketCost + 50 where rid = new.rid;
	end if;
end;
//
delimiter ;
