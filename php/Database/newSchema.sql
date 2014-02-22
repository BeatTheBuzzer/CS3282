CREATE TABLE bus_stop(
	stop_id VARCHAR(10) NOT NULL,
	type ENUM('bus','MRT'),
	name VARCHAR(40) NOT NULL,
	location VARCHAR(100),
	latitude DOUBLE,
	longitude DOUBLE,
	PRIMARY KEY(stop_id)
) ENGINE=InnoDB;

CREATE TABLE route(
	route_id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(40) NOT NULL,
	type ENUM('bus','MRT'),
	begin_time TIME,
	end_time TIME,
	PRIMARY KEY(route_id)
) ENGINE=InnoDB;

CREATE TABLE user(
	user_id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(40) NOT NULL,
	email VARCHAR(60) UNIQUE,
	password VARCHAR(40) NOT NULL,
	PRIMARY KEY(user_id)
) ENGINE=InnoDB;

-- each record consists of a bus and a stop.

CREATE TABLE schedule(
	schedule_id INT NOT NULL AUTO_INCREMENT,
	route_id INT NOT NULL,
	stop_id VARCHAR(10) NOT NULL,
	time TIME,
	PRIMARY KEY(schedule_id),
	FOREIGN KEY(route_id) REFERENCES route(route_id),
	FOREIGN KEY(stop_id) REFERENCES bus_stop(stop_id)
) ENGINE=InnoDB;

CREATE TABLE info(
	post_id INT NOT NULL AUTO_INCREMENT,
	route_id INT NOT NULL,
	stop_id VARCHAR(10) NOT NULL,
	time TIME,
	date DATE,
	crowded ENUM('yes','no'),	
	PRIMARY KEY(post_id),
	FOREIGN KEY(route_id) REFERENCES route(route_id),
	FOREIGN KEY(stop_id) REFERENCES bus_stop(stop_id)
) ENGINE=InnoDB;

CREATE TABLE bus95(
	idx INT,
	stop_id VARCHAR(10),
	PRIMARY KEY(idx),
	FOREIGN KEY(stop_id) REFERENCES bus_stop(stop_id)
)