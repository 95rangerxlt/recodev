#!/usr/bin/python

#       /*
#        * RecoDev
#        * RecoDev-Evaluator3
#        * This script is employed for evaluating the RecoDev recommender prototype.
#        * <OWNER> = Amir H. Moin (moin@in.tum.de)
#        * <ORGANIZATION> = Tehnische Universitaet Muenchen
#        * <YEAR> = 2016
#        * License: BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
#        */

#Input arguments:
#base_path
#project_name
#new_issue_text
print "Welcome to RecoDev-Evaluator3!"

base_path = sys.argv[1]
project_name = sys.argv[2]
#new_issue_text = sys.argv[3]

print "#3. Evaluation of the *hybrid* recommendation engine using single-label (i.e., normal) classification..."
print "The similarity measure of the collaborative engines uses not only the Cosine similarity measure, but alos the information of the CC lists of bug reports..."

print "Please note that for the sake of evaluation, the two engines (i.e., content-based and collaborative) work in series. However, in the recommender they always run in parallel (see RecoDev-Recommender)."

print "Engine no. 1 (content-based) starts..."

print "Loading text files..."
from sklearn.datasets import load_files
proj_new = load_files(base_path + "RecoDev-DatasetPreparation-ContentBased/" + project_name + "_new/", description="Eclipse JDT", categories=None, load_content=True, shuffle=True, encoding="latin-1", random_state=45)

print "Randomly shuffling and splitting the dataset into training and testing datasets..."
from sklearn.cross_validation import train_test_split
X_train, X_test, y_train, y_test = train_test_split(proj_new.data, proj_new.target, test_size=0.3, random_state=42)

print "Extracting features from the training dataset: Tokenization, vectorization & TF-IDF transformation..."
from sklearn.feature_extraction.text import TfidfVectorizer
vectorizer = TfidfVectorizer(lowercase=True, min_df=15, stop_words='english', analyzer='char', ngram_range=(3, 8))
X_train_vect = vectorizer.fit_transform(X_train)

print "Extracting features from the testing dataset using the same vectorizer (as for the training dataset)..."
X_test_vect = vectorizer.transform(X_test)

print "Feature selection / dimensionality reduction (using training )..."
from sklearn.feature_selection import SelectKBest
from sklearn.feature_selection import chi2
ch2 = SelectKBest(chi2, k=25000)
X_train_vect_red = ch2.fit_transform(X_train_vect, y_train)
X_test_vect_red = ch2.transform(X_test_vect)

print "Training a Support Vector Machine (SVM) classifier using LinearSVC and the training dataset..."
from sklearn.svm import LinearSVC
clf = LinearSVC(C=6.5)
clf.fit(X_train_vect_red, y_train)

print "Storing the predictions of the trained classifier on the testing dataset..."
predicted = clf.predict(X_test_vect_red)

#print "Evaluation results of the content-based engine working alone, predicting the top-1 developer: "
#
import numpy as np
#print "Accuracy: %s" % np.mean(predicted == y_test)
from sklearn.metrics import classification_report
#print "Classification report (Precision, Recall and F-Measure): "
#print(classification_report(y_test, predicted))

#print "Evaluation results of the content-based engine working alone, predicting the top-3 developers: "

#decision_matrix = clf.decision_function(X_test_vect_red)
#for i in range(0,decision_matrix.shape[1]-1):
#	sorted_labels = decision_matrix[i].argsort()
#	dev2 = sorted_labels[-2]
#	dev3 = sorted_labels[-3]
#
from copy import deepcopy
#
#predicted_group = deepcopy(predicted)
#for i in range(0, len(predicted)-1):
#	if (predicted[i] != y_test[i]):
#		if (dev2 == y_test[i]):
#			predicted_group[i] = dev2
#		if (dev3 == y_test[i]):
#			predicted_group[i] = dev3
#
#print "Accuracy: %s" % np.mean(predicted_group==y_test)
#print "For top-1 would be: %s " % np.mean(predicted==y_test)
#from sklearn.metrics import classification_report
#print "Classification report (Precision, Recall and F-Measure): "
#print(classification_report(y_test, predicted_group))
#
print "Engine no. 1 (content-based) stops..."

print "Engine no. 2 (collaborative) starts..."

print "Finding similarity of developers' profiles..."

print "Loading textual profiles of developers..."

import os
import sys
import shutil
from os import listdir
def listdir_fullpath(d):
	return [os.path.join(d, f) for f in os.listdir(d)]

dev_filenames = listdir_fullpath(base_path + "RecoDev-DatasetPreparation-Collaborative/" + project_name + "_new/")
dev_filenames_nopath = listdir(base_path + "RecoDev-DatasetPreparation-Collaborative/" + project_name + "_new/")

print "Extracting features from the developers' textual profiles: Tokenization, vectorization & TF-IDF transformation..."
dev_profiles_vectorizer = TfidfVectorizer(input='filename', lowercase=True, min_df=15, stop_words='english', analyzer='char', ngram_range=(3, 8))
dev_profiles_features = dev_profiles_vectorizer.fit_transform(dev_filenames)

print "Computing the similarity of developers' textual profiles according to the Cosine similarity measure and the TF-IDF feature vectors..."
from sklearn.metrics.pairwise import cosine_similarity
cosine=cosine_similarity(dev_profiles_features, dev_profiles_features)
#cosine=(dev_profiles_features * dev_profiles_features.T).A

dev_sim = np.zeros(shape=(len(proj_new.target_names),2))
for i in range(0,len(cosine)-1):
	dev_sorted_indices = cosine[i].argsort()
	dev_first_similar = dev_filenames_nopath[dev_sorted_indices[-2]].rpartition('.')[0]
	dev_second_similar = dev_filenames_nopath[dev_sorted_indices[-3]].rpartition('.')[0]
	dev_index = proj_new.target_names.index(dev_filenames_nopath[i].rpartition('.')[0])
	dev_first_similar_index = proj_new.target_names.index(dev_first_similar)
	dev_second_similar_index = proj_new.target_names.index(dev_second_similar)
	dev_sim[dev_index] = [dev_first_similar_index, dev_second_similar_index]


print "Computing the similarity of developers based on the number of co-occurrences in the CC lists of issues..."
import MySQLdb
dbc = MySQLdb.connect(user='recodev', passwd='ahm20kdd16', host='127.0.0.1', db='recodev')
cursor = dbc.cursor()
cursor.execute("SELECT bug_id, cc_list FROM " + project_name + "_issues WHERE cc_list NOT like '\[\]';")
cc_lists = cursor.fetchall()

dev_cc = np.zeros(shape=(len(proj_new.target_names),len(proj_new.target_names)))
for i in range(0, len(cc_lists)-1):
	str = cc_lists[i]
	devs = str[1].split(",")
	for j in range(0, len(devs)-1):
		devs[j] = devs[j].translate(None, "[] ")
		name = devs[j].partition("@")[0]
		if name in proj_new.target_names:
			devs[j] = proj_new.target_names.index(name)
		else:
			devs[j] = -1
	for j in range(0, len(devs)-1):
		if devs[j] != -1:
			row = devs[j]
			for k in range(0, len(devs)-1):
				if devs[k] != -1:			
					dev_cc[row][devs[k]] += 1



for i in range(0, dev_cc.shape[0]):
	dev_cc_i_sorted = np.argsort(dev_cc[i])
	dev_cc_max1 = dev_cc_i_sorted[-1]
	dev_cc_max2 = dev_cc_i_sorted[-2]
	if dev_cc_max1 == i:
		dev_cc_rec = dev_cc_max2
	else:
		dev_cc_rec = dev_cc_max1
	if (dev_sim[i][0] != dev_cc_rec and dev_sim[i][1] != dev_cc_rec):
		dev_sim[i][1] = dev_cc_rec

 

print "Refining the prediction results of the first engine (i.e., content-based) using the similarity recommendations of the second engine (i.e., collaborative)..."

predicted2 = deepcopy(predicted)
for i in range(0, len(predicted)-1):
	if (predicted[i] != y_test[i]):
		if (dev_sim[predicted[i]][0] == y_test[i]):
			predicted2[i] = dev_sim[predicted[i]][0]
		if (dev_sim[predicted[i]][1] == y_test[i]):
			predicted2[i] = dev_sim[predicted[i]][1]

print "The accuracy is: %s" % np.mean(predicted2==y_test)
from sklearn.metrics import classification_report
print "Classification report: "
print(classification_report(y_test, predicted2))

print "Engine no. 2 (collaborative) stops..."

print "Finished"
