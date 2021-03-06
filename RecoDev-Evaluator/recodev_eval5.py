#!/usr/bin/python

#       /*
#        * RecoDev
#        * RecoDev-Evaluator5
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
print "Welcome to RecoDev-Evaluator5!"

base_path = sys.argv[1]
project_name = sys.argv[2]
#new_issue_text = sys.argv[3]

print "#5. Evaluation of the content-based recommendation engine using ***multi-label classification***, recommending the top-3 developers..."

print "Loading text files..."
from sklearn.datasets import load_files
proj_new = load_files(base_path + "RecoDev-DatasetPreparation-ContentBased-MultLabel/" + project_name + "_new/", description="Eclipse JDT", categories=None, load_content=True, shuffle=True, encoding="latin-1", random_state=45)

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

print "Evaluation results of the content-based engine working alone (predicting only one developer): "

import numpy as np
print "Accuracy: %s" % np.mean(predicted == y_test)
from sklearn.metrics import classification_report
print "Classification report (Precision, Recall and F-Measure): "
print(classification_report(y_test, predicted))

print "Evaluation results of the content-based engine (predicting the top-3 developers): "

decision_matrix = clf.decision_function(X_test_vect_red)
for i in range(0,decision_matrix.shape[1]-1):
	sorted_labels = decision_matrix[i].argsort()
	dev2 = sorted_labels[-2]
	dev3 = sorted_labels[-3]

from copy import deepcopy

predicted_group = deepcopy(predicted)
for i in range(0, len(predicted)-1):
	if (predicted[i] != y_test[i]):
		if (dev2 == y_test[i]):
			predicted_group[i] = dev2
		if (dev3 == y_test[i]):
			predicted_group[i] = dev3

print "Accuracy: %s" % np.mean(predicted_group==y_test)
print "Top-1 would be: %s " % np.mean(predicted==y_test)
from sklearn.metrics import classification_report
print "Classification report (Precision, Recall and F-Measure): "
print(classification_report(y_test, predicted_group))

print "Finished"
