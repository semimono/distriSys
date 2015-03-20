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

CREATE OR REPLACE FUNCTION IssueBook() RETURNS trigger AS '
	BEGIN
		NEW.DueDate := NEW.IssueDate+30;
		RETURN NEW;
	END;
' LANGUAGE plpgsql;

CREATE TRIGGER IssueBook BEFORE INSERT OR UPDATE
	ON Books2Students FOR EACH ROW EXECUTE PROCEDURE IssueBook();