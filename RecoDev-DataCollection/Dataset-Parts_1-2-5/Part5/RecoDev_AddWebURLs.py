#	/*
#	 * RecoDev
#	 * AddWebURLs.py
#	 * This script is used for adding the URLs to the developers table. See the accompanying readme file of RecoDev-DataCollection #for more info.
#	 * <OWNER> = Amir H. Moin (moin@in.tum.de)
#	 * <ORGANIZATION> = Tehnische Universität München
#	 * <YEAR> = 2015
#	 * License: BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
#	 */

import MySQLdb

dbc = MySQLdb.connect(user='recodev', passwd='ahm20kdd16', host='127.0.0.1', db='recodev')

cursor = dbc.cursor()

import csv
with open('devs.csv', 'rb') as csvfile:
	csvReader = csv.reader(csvfile, delimiter=',')
	for row in csvReader:
		query = ("UPDATE eclipse_jdt_developers SET web_url = '" + row[2] + "' WHERE dev_user_name LIKE '%" + row[0][0:row[0].index('@')] + "%'")	
		cursor.execute(query);
cursor.close()		
dbc.commit()
dbc.close()
