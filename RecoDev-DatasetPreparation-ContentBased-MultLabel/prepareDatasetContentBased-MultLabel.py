#!/usr/bin/python

#	/*
#	 * RecoDev
#	 * RecoDev-DatasetPreparation-ContentBased-MultLabel
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

if not os.path.exists(base_path + "RecoDev-DatasetPreparation-ContentBased-MultLabel/" + project_name):
                shutil.copytree(base_path + "RecoDev-DatasetPreparation-ContentBased/" + project_name, base_path + "RecoDev-DatasetPreparation-ContentBased-MultLabel/" + project_name, symlinks=False, ignore=None)

cursor.execute("SELECT bug_id1, bug_id2 FROM " + project_name + "_issues_relation;")
issues_relations = cursor.fetchall()
for row in issues_relations:
	cursor.execute("SELECT resolver_dev FROM " + project_name + "_issues WHERE bug_id=" + str(row[0]) + ";")
	dev1 = cursor.fetchone()
	rows_count = cursor.execute("SELECT resolver_dev FROM " + project_name + "_issues WHERE bug_id=" + str(row[1]) + ";")
	if rows_count > 0:
		dev2 = cursor.fetchone()	
		if (os.path.exists(base_path + "RecoDev-DatasetPreparation-ContentBased-MultLabel/" + project_name  + "/" + dev1[0] + "/" + str(row[0]) + ".txt")) and (os.path.exists(base_path + "RecoDev-DatasetPreparation-ContentBased-MultLabel/" + project_name  + "/" + dev2[0] + "/" + str(row[1]) + ".txt")) and (dev1 != dev2):
    			shutil.copy(base_path + "RecoDev-DatasetPreparation-ContentBased-MultLabel/" + project_name  + "/" + dev2[0] + "/" + str(row[1]) + ".txt", base_path + "RecoDev-DatasetPreparation-ContentBased-MultLabel/" + project_name  + "/" + dev1[0] + "/")

cursor.close()		
dbc.commit()
dbc.close()
