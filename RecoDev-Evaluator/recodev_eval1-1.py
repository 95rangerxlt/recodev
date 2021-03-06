#!/usr/bin/python

#       /*
#        * RecoDev
#        * RecoDev-Evaluator1-1
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
print "Welcome to RecoDev-Evaluator1-1!"

base_path = sys.argv[1]
project_name = sys.argv[2]
#new_issue_text = sys.argv[3]

print "#1-1. Evaluation of the content-based recommendation engine using single-label (normal) classification, predicting the top-1 developer..."

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

print "Evaluating the trained classifier using the testing dataset, predicting the top-1 developer..."
predicted = clf.predict(X_test_vect_red)
import numpy as np
print "The accuracy is: %s" % np.mean(predicted == y_test)
from sklearn.metrics import classification_report
print "Classification report: "
print(classification_report(y_test, predicted))

print "Finished"
