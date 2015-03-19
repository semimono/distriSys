CREATE TABLE Students (
 StudentID integer PRIMARY KEY,
 FName varchar,
 LName varchar,
 Degree varchar
);

CREATE TABLE Books (
 ISBN integer PRIMARY KEY,
 Name varchar,
 Year integer,
 Copies integer
);

CREATE TABLE Books2Students (
 StudentID integer REFERENCES Students (StudentID),
 ISBN integer REFERENCES Books (ISBN),
 IssueDate date,
 DueDate date C,
 PRIMARY KEY (StudentID, ISBN, IssueDate)
);

CREATE TRIGGER DueDateUpdate AFTER INSERT OR UPDATE OF IssueDate
ON Books2Students
EXECUTE PROCEDURE UPDATE (DueDate) VALUES (IssueDate +30);
