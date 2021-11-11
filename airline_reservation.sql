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
 planeID int references Planes(planeID) on delete cascade on update cascade,
 startID int references Location(locationID) on delete cascade on update cascade,
 destID int references Location(locationID) on delete cascade on update cascade,
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
 currentFlight int references Flights(fID) on delete cascade on update cascade
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
 fID int references Flights(fID) on delete cascade on update cascade,
 uID int references Passenger(uID) on delete cascade on update cascade,
 ticketType int,
 ticketCost float
);

# Triggers

## If baggage weight is over limit, charge extra on ticket

drop trigger if exists overweightBagCharge;
delimiter //
create trigger overweightBagCharge
after insert on reservations
for each row
begin
    if (select totalBagWeight from passenger where new.uid = passenger.uid) > 50 then
        update reservations set ticketCost = ticketCost + 50 where rid = new.rid;
    end if;
end;
//
delimiter ;

## Update ticket price if passenger gets pushed down in class

drop trigger if exists ticketChange;
delimiter //
create trigger ticketChange
after update on reservations
for each row
begin
    if new.ticketType > old.ticketType then
    update reservations set ticketCost = ticketCost - (new.ticketType - old.ticketType) * 100  where rid = old.rid;
    end if;
end;
//
delimiter ;

## Update ticket type if desired section is already full (next highest class is checked for room first before lowest/highest class for downgrades/upgrades)

drop trigger if exists seatChange;
delimiter //
create trigger seatChange
after insert on reservations
for each row
begin
    if new.ticketType = 2 and (select count(*) from reservations where fid = new.fid and ticketType = 2) = (select numEcon from planes natural join flights where fid = new.fid) then
		if (select count(*) from reservations where fid = new.fid and ticketType = 1) < (select numBusiness from planes natural join flights where fid = new.fid) then
			update reservations set ticketType = 1 where rid = new.rid;
		else
			update reservations set ticketType = 0 where rid = new.rid;
		end if;
    end if;
    if new.ticketType = 1 and (select count(*) from reservations where fid = new.fid and ticketType = 1) = (select numBusiness from planes natural join flights where fid = new.fid) then
		if (select count(*) from reservations where fid = new.fid and ticketType = 0) < (select numFirst from planes natural join flights where fid = new.fid) then
			update reservations set ticketType = 0 where rid = new.rid;
		else
			update reservations set ticketType = 2 where rid = new.rid;
		end if;
    end if;
    if new.ticketType = 0 and (select count(*) from reservations where fid = new.fid and ticketType = 0) = (select numFirst from planes natural join flights where fid = new.fid) then
		if (select count(*) from reservations where fid = new.fid and ticketType = 1) < (select numBusiness from planes natural join flights where fid = new.fid) then
			update reservations set ticketType = 1 where rid = new.rid;
		else
			update reservations set ticketType = 2 where rid = new.rid;
		end if;
    end if;
end;
//
delimiter ;

delimiter //

drop procedure if exists movetoArchive;

CREATE PROCEDURE movetoArchive (IN target VARCHAR(50), out result ) 
BEGIN
	SELECT * FROM Reservations WHERE date(updatedAt) < target; 
END;

delimiter;

