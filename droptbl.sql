-- Include your drop table DDL statements in this file.
-- Make sure to terminate each statement with a semicolon (;)

-- LEAVE this statement on. It is required to connect to your database.
CONNECT TO cs421;

-- Remember to put the drop table ddls for the tables with foreign key references
--    ONLY AFTER the parent tables has already been dropped (reverse of the creation order).

-- This is only an example of how you add drop table ddls to this file.
--   You may remove it.
DROP TABLE MYTEST01;
DROP TABLE Stadium;
DROP TABLE SeatInStadium;
DROP TABLE Seat;
DROP TABLE Buyer;
DROP TABLE Ticket;
DROP TABLE Match;
DROP TABLE Referee;
DROP TABLE RefMatch;
DROP TABLE Team;
DROP TABLE TeamMember;
DROP TABLE Player;
DROP TABLE Coach;
DROP TABLE Goal;
DROP TABLE PlaysMatch;

