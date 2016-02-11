#!/usr/bin/python

#	/*
#	 * RecoDev
#	 * RecoDev-DatasetPreparation-Collaborative
#	 * This script is used for preparing the dataset that has been collected by RecoDev-DataCollection, in order to be used by the RecoDev's content-based engine.
#	 * <OWNER> = Amir H. Moin (moin@in.tum.de)
#	 * <ORGANIZATION> = Tehnische Universitaet Muenchen
#	 * <YEAR> = 2016
#	 * License: BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
#	 */

#Input arguments:
#base_path
#project_name

import MySQLdb
import os
import sys
import shutil
base_path = sys.argv[1]
project_name = sys.argv[2]
dbc = MySQLdb.connect(user='recodev', passwd='ahm20kdd16', host='127.0.0.1', db='recodev')
cursor = dbc.cursor()

if not os.path.exists(base_path + "RecoDev-DatasetPreparation-ContentBased-Collaborative/" + project_name):
	os.makedirs(base_path + "RecoDev-DatasetPreparation-Collaborative/" + project_name)

cursor.execute("SELECT dev_user_name FROM " + project_name + "_developers;")
developers = cursor.fetchall()
for row in developers:
	if not os.path.exists(base_path + "RecoDev-DatasetPreparation-Collaborative/" + project_name + "/" + str(row[0]) + ".txt"):
		with open(base_path + "RecoDev-DatasetPreparation-Collaborative/" + project_name + "/" + str(row[0]) + ".txt", 'w') as developer_file:
			cursor.execute("SELECT bug_id FROM " + project_name + "_issues WHERE resolver_dev LIKE '%" + str(row[0]) + "%';")
			issue_ids = cursor.fetchall()
			
			searchfile = open(base_path + "RecoDev-DataCollection/Dataset-Part4/commitLogs-" + project_name + ".txt", "r")
			flag = False
                        for line in searchfile:
	                        if str(row[0]).lower() in line.lower(): 
					if(flag == False): 
						tmp_str = line[line.rfind(";#;"):]
						developer_file.write(tmp_str[tmp_str.find("@")+1:] + "\n")
						flag = True
					developer_file.write(line[0:line.find(";#;")] + "\n")
				for prow in issue_ids:
					if str(prow[0]) in line:
						developer_file.write("\n" + line[0:line.find(";#;")])
			searchfile.close()
			
cursor.close()		
dbc.commit()
dbc.close()
