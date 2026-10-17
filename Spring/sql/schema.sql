
CREATE TABLE data.users (
        username VARCHAR(40) NOT NULL,
        email VARCHAR(50) NOT NULL,
        password_hash NCHAR(60) NOT NULL,
        PRIMARY KEY (username)
);

