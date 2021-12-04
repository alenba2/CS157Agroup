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
 totalBagWeight float,
 CONSTRAINT CHECK (numBags < 3),
 CONSTRAINT CHECK (totalBagWeight < 100)
);

drop table if exists Flights;
create table Flights
(
 fID int primary key AUTO_INCREMENT,
 planeID int references Planes(planeID) on delete cascade on update cascade,
 startID int references Location(locationID) on delete cascade on update cascade,
 destID int references Location(locationID) on delete cascade on update cascade,
 time timestamp,
 CONSTRAINT CHECK (destID <> startID)
);

drop table if exists Planes;
create table Planes
(
 planeID int primary key AUTO_INCREMENT,
 numPassengers int,
 numEcon int,
 numBusiness int,
 numFirst int,
 currentFlight int unique key references Flights(fID) on delete cascade on update cascade,
 CONSTRAINT CHECK (numPassengers < 150),
 CONSTRAINT CHECK (numEcon < 50),
 CONSTRAINT CHECK (numBusiness < 50),
 CONSTRAINT CHECK (numFirst < 50),
 CONSTRAINT CHECK ((numFirst+numBusiness+numEcon)=numPassengers)
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
 ticketCost float,
 CONSTRAINT fID unique (fID, uID),
 updatedAt timestamp
);

drop table if exists Archive;
create table Archive
(
 rID int,
 fID int references Flights(fID),
 uID int references Passenger(uID),
 ticketType int,
 ticketCost float,
 CONSTRAINT fID unique (fID, uID),
 updatedAt timestamp
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
before update on reservations
for each row
begin
    if new.ticketType > old.ticketType then
    set new.ticketCost := old.ticketCost - (new.ticketType - old.ticketType) * 100;
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

## Copies old data to be stored into Archive 

drop procedure if exists movetoArchive;

DELIMITER //

CREATE PROCEDURE movetoArchive(IN date CHAR(10)) 

BEGIN
    insert into Archive SELECT * FROM Reservations WHERE date(updatedAt) < date;
END//

DELIMITER ;

insert into Passenger values ('1', 'Allen', 2, 34);
#insert into Passenger values ('2', 'Stephanie', 3, 34);
#insert into Passenger values ('3', 'Max', 2, 200);
insert into Planes values ('1', 75, 25, 25, 25, 1);
#insert into Planes values ('2', 200, 25, 25, 25, 1);
#insert into Planes values ('3', 100, 60, 20, 20, 1);
#insert into Planes values ('4', 100, 40, 20, 40, 1);
#insert into Planes values ('5', 100, 40, 20, 40, 2);
#insert into Flights values ('1', '1', 1, 2, '00:00:00');
#insert into Flights values ('1', '1', 1, 1, '00:00:00');
insert into Reservations values ('1', '1', '1', 1, 50,'1990-01-01 10:10:10');
insert into Reservations values ('2', '1', '2', 1, 50,'1999-01-01 10:10:10');
#insert into Reservations values ('3', '1', '1', 1, 50);
