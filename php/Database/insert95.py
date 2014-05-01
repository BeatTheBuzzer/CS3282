#!/usr/bin/python
from datetime import datetime, timedelta, date
from random import randint
import MySQLdb
import os
db = MySQLdb.connect("localhost","crowdapp","cs3282","crowdapp_db")
cursor = db.cursor()
#rootdir = 'test'
rootdir = './bus95in24hour'
start = '2014-03-28'
dateFMT = '%Y-%m-%d'
#s1 = '00:01:00'
#s2 = '00:02:00'
#s = ['00:01:00','00:02:00']
timeFMT = '%H:%M:%S'
crowded = 'yes'
for i in range(0,9):
	tdelta = datetime.strptime(start,dateFMT) + timedelta(days=i)
	current = date.strftime(tdelta,dateFMT)
	print current
	for subdir, dirs, files in os.walk(rootdir):
		for file in files:
			print file
			f = open(subdir+'/'+file,'r')
			for line in f:
				time = line[:8]
				for j in range(2):
					if randint(0,1) == 0:
						tdelta = datetime.strptime(time,timeFMT) + timedelta(minutes=1)
					else:
						tdelta = datetime.strptime(time,timeFMT) - timedelta(minutes=2)
					
					time2 = date.strftime(tdelta,timeFMT)
					#print time2
					if randint(0,1) == 0:
						crowded = 'yes'
					else:
						crowded = 'no'
					#print crowded
					sql = "INSERT INTO info(route_id, stop_id, time, date, crowded) VALUES ('%d', '%s', '%s','%s', '%s')" % (1,file,time2,current,crowded)
					try:
						cursor.execute(sql)
						db.commit()
					except:
						db.rollback()
				#INSERT INTO info (route_id,stop_id,time,date,crowded) VALUES()
			f.close()
db.close()
