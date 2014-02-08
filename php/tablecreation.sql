USE crowd_db;
CREATE TABLE route(
	route_id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(40) NOT NULL,
	type ENUM('bus','MRT'),
	begin_time TIME,
	end_time TIME,
	PRIMARY KEY(route_id)
);
CREATE TABLE bus_stop(
	stop_id VARCHAR(10) NOT NULL,
	type ENUM('bus','MRT'),
	name VARCHAR(40) NOT NULL,
	location VARCHAR(100),
	PRIMARY KEY(stop_id)
);
CREATE TABLE stop_time(
	stop_time_id INT NOT NULL AUTO_INCREMENT,
	route_id INT NOT NULL,
	stop_id VARCHAR(10) NOT NULL,
	time TIME,
	PRIMARY KEY(stop_time_id),
	FOREIGN KEY(route_id) REFERENCES route(route_id),
	FOREIGN KEY(stop_id) REFERENCES route(stop_id)
);
CREATE TABLE user(
	user_id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(40),
	email VARCHAR(60) UNIQUE,
	password VARCHAR(40),
	PRIMARY KEY(user_id)
);
CREATE TABLE post(
	post_id INT NOT NULL AUTO_INCREMENT,
	time TIME,
	content VARCHAR(100),
	author_id INT NOT NULL,
	stop_time_id VARCHAR(10) NOT NULL,
	route_id INT NOT NULL,
	PRIMARY KEY(post_id),
	FOREIGN KEY(author_id) REFERENCES user(user_id),
	FOREIGN KEY(route_id) REFERENCES route(route_id),
	FOREIGN KEY(stop_time_id) REFERENCES stop_time(stop_time_id)
);
CREATE TABLE rating(
	rating_id INT NOT NULL AUTO_INCREMENT,
	author_id INT NOT NULL,
	reviewer_id INT NOT NULL,
	post_id INT NOT NULL,
	rate INT NOT NULL,
	PRIMARY KEY(rating_id),
	CONSTRAINT rate CHECK (rate > 0 AND rate < 6),
	FOREIGN KEY(author_id) REFERENCES user(user_id),
	FOREIGN KEY(reviewer_id) REFERENCES user(user_id),
	FOREIGN KEY(post_id) REFERENCES post(post_id)
);