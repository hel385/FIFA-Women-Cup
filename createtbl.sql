-- Include your create table DDL statements in this file.
-- Make sure to terminate each statement with a semicolon (;)

-- LEAVE this statement on. It is required to connect to your database.
CONNECT TO cs421;

-- Remember to put the create table ddls for the tables with foreign key references
--    ONLY AFTER the parent tables has already been created.

-- This is only an example of how you add create table ddls to this file.
--   You may remove it.
CREATE TABLE MYTEST01
(
  id INTEGER NOT NULL
 ,value INTEGER
 ,PRIMARY KEY(id)
);


CREATE TABLE Stadium
(
	sName VARCHAR(250) NOT NULL,
	location VARCHAR(250),
	capacity INTEGER, 
	PRIMARY KEY (sName)
);

CREATE TABLE Seat 
(
	section VARCHAR(250) NOT NULL,
	row_num INTEGER NOT NULL,
	sName VARCHAR(250),
	PRIMARY KEY (section, row_num),
	FOREIGN KEY (sName) REFERENCES Stadium
);

CREATE TABLE SeatInStadium 
(
	sName VARCHAR(250) NOT NULL,
	section VARCHAR(250) NOT NULL,
        row_num INTEGER NOT NULL,
	PRIMARY KEY (sName, section, row_num),
	FOREIGN KEY (sName) REFERENCES Stadium,
	FOREIGN KEY (section, row_num) REFERENCES Seat
);

CREATE TABLE Buyer 
(
	confirmationNum INTEGER NOT NULL,
	email VARCHAR(250),
	bName VARCHAR(250),
	totalAmount INTEGER,
	PRIMARY KEY (confirmationNum)
);

CREATE TABLE Team 
(
	country VARCHAR(250) NOT NULL,
	NA VARCHAR(250),
	tGroup VARCHAR(1),
	url VARCHAR(250),
	PRIMARY KEY (country)
);

CREATE TABLE Match 
(
	matchId INTEGER NOT NULL,
	mRound INTEGER,
	mLength INTEGER,
	mDate DATE,
	mTime TIME, 
	sName VARCHAR(250) NOT NULL,
	fCountry VARCHAR(250) NOT NULL,
	fGoal INTEGER,
	sCountry VARCHAR(250) NOT NULL,
	sGoal INTEGER,
	CHECK (matchId<=64),
	PRIMARY KEY (matchId),
	FOREIGN KEY (sName) REFERENCES Stadium,
	FOREIGN KEY (sCountry) REFERENCES Team,
	FOREIGN KEY (fCountry) REFERENCES Team
);	

CREATE TABLE Ticket 
(
	ticketNum VARCHAR(250) NOT NULL,
	availability BOOLEAN,
	tType VARCHAR(250),
	section VARCHAR(250) NOT NULL,
	row_num INTEGER NOT NULL,
	confirmationNum INTEGER NOT NULL,
	matchId INTEGER NOT NULL,
	tPrice INTEGER,
	PRIMARY KEY (ticketNum),
	FOREIGN KEY (section, row_num) REFERENCES Seat,
	FOREIGN KEY (confirmationNum) REFERENCES Buyer,
	FOREIGN KEY (matchId) REFERENCES Match
);

CREATE TABLE Referee
(
        rId INTEGER NOT NULL,
        yearsOfExp INTEGER,
        country VARCHAR(250),
        rName VARCHAR(250),
        PRIMARY KEY (rId)
);

CREATE TABLE RefMatch
(
        rId INTEGER NOT NULL,
        matchId INTEGER NOT NULL,
        role VARCHAR(250),
        PRIMARY KEY (rId, matchId),
        FOREIGN KEY (rId) REFERENCES Referee,
        FOREIGN KEY (matchId) REFERENCES Match
);

CREATE TABLE TeamMember
(
        tId INTEGER NOT NULL,
        tName VARCHAR(250),
        DOB DATE,
        country VARCHAR(250) NOT NULL,
        PRIMARY KEY (tId),
        FOREIGN KEY (country) REFERENCES Team
);

CREATE TABLE Player
(
        tId INTEGER NOT NULL,
        position VARCHAR(250),
        shirtNum INTEGER,
        PRIMARY KEY (tId),
        FOREIGN KEY (tId) REFERENCES TeamMember

);

CREATE TABLE Coach
(
        role VARCHAR(250),
        tId INTEGER NOT NULL,
        PRIMARY KEY (tId),
        FOREIGN KEY (tId) REFERENCES TeamMember
);

CREATE TABLE Goal(
        matchId INTEGER NOT NULL,
        occurence INTEGER,
        tId INTEGER NOT NULL,
        minute INTEGER NOT NULL,
        duringPen BOOLEAN,
        PRIMARY KEY (matchId, minute),
        FOREIGN KEY (matchId) REFERENCES Match,
        FOREIGN KEY (tId) REFERENCES Player
);

CREATE TABLE PlaysMatch
(
        matchId INTEGER NOT NULL,
        tId INTEGER NOT NULL,
        timeIn TIME,
        timeOut TIME,
        positionPlayed VARCHAR(250),
        yellowCards INTEGER,
        receiveRed BOOLEAN,
        PRIMARY KEY (tId, matchId),
        FOREIGN KEY (tId) REFERENCES Player,
        FOREIGN KEY (matchId) REFERENCES Match
);
