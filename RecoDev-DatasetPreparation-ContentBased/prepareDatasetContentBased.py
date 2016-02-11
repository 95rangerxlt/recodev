#!/usr/bin/python

#	/*
#	 * RecoDev
#	 * RecoDev-DatasetPreparation-ContentBased
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

if not os.path.exists(base_path + "RecoDev-DatasetPreparation-ContentBased/" + project_name):
                os.makedirs(base_path + "RecoDev-DatasetPreparation-ContentBased/" + project_name)

cursor.execute("SELECT dev_user_name FROM " + project_name + "_developers;")
developers = cursor.fetchall()
for row in developers:
	if not os.path.exists(base_path + "RecoDev-DatasetPreparation-ContentBased/" + project_name  + "/" + row[0]):
    		os.makedirs(base_path + "RecoDev-DatasetPreparation-ContentBased/" + project_name + "/" + row[0])
		cursor.execute("SELECT bug_id FROM " + project_name + "_issues WHERE resolver_dev LIKE '%" + row[0]  + "%';")
		issues = cursor.fetchall()
		for prow in issues:
			shutil.copyfile(base_path + "RecoDev-DataCollection/Dataset-Part3/issues-" + project_name + "/" + str(prow[0]) + ".txt", 
base_path + "RecoDev-DatasetPreparation-ContentBased/" + project_name + "/" + row[0] + "/" + str(prow[0]) + ".txt")	
			with open(base_path + "RecoDev-DatasetPreparation-ContentBased/" + project_name + "/" + row[0] + "/" + str(prow[0]) + ".txt", "a") as issue_file:
				cursor.execute("SELECT summary, keywords FROM " + project_name + "_issues WHERE bug_id=" + str(prow[0]) +";")
				summary_keywords=cursor.fetchone()
    				issue_file.write("\n\n" + summary_keywords[0] + "\n\n" + (summary_keywords[1])[1:-1])
				searchfile = open(base_path + "RecoDev-DataCollection/Dataset-Part4/commitLogs-" + project_name + ".txt", "r")
				for line in searchfile:
    					if str(prow[0]) in line: issue_file.write("\n\n" + line[0:line.find(";#;")])
				searchfile.close()
				

cursor.close()		
dbc.commit()

dbc.close()
