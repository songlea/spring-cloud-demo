CREATE TABLE users ( `username` VARCHAR ( 50 ) NOT NULL PRIMARY KEY, `password` varchar( 500 ) NOT NULL, `enabled` tinyint(1) NOT NULL );
CREATE TABLE authorities (
  `username` VARCHAR ( 50 ) NOT NULL,
  `authority` varchar ( 50 ) NOT NULL,
  CONSTRAINT fk_authorities_users FOREIGN KEY ( username ) REFERENCES users ( username )
);
CREATE UNIQUE INDEX ix_auth_username ON authorities ( username, authority );