USE crowd_db;
INSERT INTO route (name, type, begin_time, end_time)
VALUES(	'95','bus','07:00:00','23:30:00');

INSERT INTO bus_stop 
VALUES('B18331','bus','KENT RIDGE STN','LOWER KENT RIDGE RD');

INSERT INTO stop_time (route_id, stop_id, time)
VALUES(1,'B18331','07:00:00');

INSERT INTO user (name, email, password)
VALUES('Kyle','test@example.com', 'Kyle');

INSERT INTO user (name,email, password)
VALUES('Lowry','test2@example.com','Kyle');

INSERT INTO post (time,content, author_id, stop_time_id, route_id)
VALUES('07:00:00','crowded',1,1,1);

INSERT INTO rating (author_id, reviewer_id, post_id, rate) 
VALUES(1,2,1,3);