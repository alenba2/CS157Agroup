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
 #CONSTRAINT fID unique (fID, uID),
 updatedAt timestamp DEFAULT CURRENT_TIMESTAMP
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

insert into reservations (rid, fid, uid, ticketType, ticketCost) values (1, 1, 1, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (2, 1, 2, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (3, 1, 3, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (4, 1, 4, 0, 150);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (5, 1, 5, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (6, 1, 6, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (7, 1, 7, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (8, 1, 8, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (9, 1, 9, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (10, 1, 10, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (11, 1, 11, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (12, 1, 12, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (13, 1, 13, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (14, 1, 14, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (15, 1, 15, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (16, 1, 16, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (17, 1, 17, 0, 150);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (18, 1, 18, 0, 150);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (19, 1, 19, 0, 150);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (20, 1, 20, 0, 150);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (21, 2, 2, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (22, 2, 4, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (23, 2, 6, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (24, 2, 8, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (25, 2, 10, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (26, 2, 12, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (27, 3, 1, 0, 150);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (28, 3, 3, 0, 150);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (29, 3, 5, 0, 150);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (30, 3, 7, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (31, 3, 9, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (32, 3, 11, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (33, 3, 13, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (34, 3, 15, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (35, 3, 17, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (36, 3, 19, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (37, 3, 20, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (38, 4, 2, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (39, 4, 4, 1, 100);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (40, 4, 6, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (41, 4, 8, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost) values (42, 4, 10, 2, 50);
insert into reservations (rid, fid, uid, ticketType, ticketCost,updatedAt) values (43, 4, 8, 2, 50,'1990-01-01 00:00:00');
insert into reservations (rid, fid, uid, ticketType, ticketCost,updatedAt) values (44, 4, 10, 2, 50,'1990-01-01 00:00:00');
insert into Passenger values ('1', 'Allen', 2, 63);
insert into Passenger values ('2', 'Stephanie', 1, 15);
insert into Passenger values ('3', 'Max', 2, 72);
insert into Passenger values ('4', 'Suneuy', 2, 55);
insert into Passenger values ('5', 'Matthew', 1, 42);
insert into Passenger values ('6', 'Adriana', 1, 25);
insert into Passenger values ('7', 'Sophia', 1, 44);
insert into Passenger values ('8', 'Tyron', 2, 89);
insert into Passenger values ('9', 'Francis', 1, 11);
insert into Passenger values ('10', 'Victoria', 2, 86);
insert into Passenger values ('11', 'Bradley', 1, 27);
insert into Passenger values ('12', 'Hashir', 2, 68);
insert into Passenger values ('13', 'Bryan', 2, 60);
insert into Passenger values ('14', 'Kobe', 1, 43);
insert into Passenger values ('15', 'Koa', 1, 6);
insert into Passenger values ('16', 'Aditi', 0, 0);
insert into Passenger values ('17', 'Akaash', 1, 33);
insert into Passenger values ('18', 'Gabriel', 2, 76);
insert into Passenger values ('19', 'Ivy', 2, 94);
insert into Passenger values ('20', 'Nicholas', 2, 77);
#insert into Passenger values ('2', 'Stephanie', 3, 34);

#insert into Passenger values ('3', 'Max', 2, 200);
insert into Planes values ('1', 20, 5, 5, 10, 1);
insert into Planes values ('2', 6, 0, 4, 2, 2);
insert into Planes values ('3', 11, 3, 5, 3, 3);
insert into Planes values ('4', 5, 0, 2, 3, 4);
#insert into Planes values ('2', 200, 25, 25, 25, 1);
#insert into Planes values ('3', 100, 60, 20, 20, 1);
#insert into Planes values ('4', 100, 40, 20, 40, 1);
#insert into Planes values ('5', 100, 40, 20, 40, 2);
insert into Flights values ('1', '1', 1, 2, '1990-01-01 00:00:00');
insert into Flights values ('2', '2', 2, 3, '1990-01-07 08:00:00');
insert into Flights values ('3', '3', 2, 4, '1990-01-07 09:00:00');
insert into Flights values ('4', '4', 4, 3, '1990-01-10 10:00:00');
#insert into Flights values ('1', '1', 1, 1, '00:00:00');
#insert into Reservations values ('3', '1', '1', 1, 50);
