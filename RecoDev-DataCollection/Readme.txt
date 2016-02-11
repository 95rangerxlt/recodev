This prototype creates the dataset that will be used by the RecoDev prototype. The dataset created by this protoype has 5 parts:

-Part 1: Issue profiles (the MySQL part): Issue profiles excluding the description, comments, and related commit logs.

-Part 2: Developer profiles (the MySQL part): Developer profiles excluding the commit logs of developers and the info of social netsworks and blogs.

-Part 3: Issue profiles (the unstructured textual part): Issue profiles including the description and comments of issues.

-Part 4: The commit logs of developers in the revision control system (used for both issues profiles and developers profiles).

-Part 5: The URLs of the social networking profiles and/or personal blogs of developers. 

*Assumptions:

1. A MySQL database user called "recodev" with password "ahm20kdd16" exists and all priviledges are granted to it on the database called "recodev". This database need not to exist.

2. A CSV (Comma Separated Values) file called "CSV-X.csv", where X equals the project name (e.g., eclipse_jdt), already exists in the path.

Note that the CSV files are created using the web-based query feature of the Bugzilla systems, e.g., https://bugs.eclipse.org/bugs/query.cgi or https://bugzilla.mozilla.org/query.cgi.

3. A directory called "issues-X", where X equals the project name (e.g., eclipse_jdt), already exists in the path.

4. A text file called "commitLogs-X.txt", where X equals the project name (e.g., eclipse_jdt), already exists in the path.

Note that the text files are created using the following commands (e.g., in the terminal of a Linux system):

	git log --all --pretty=format:"%s ;#; %an ;#; %ae" > commitLogs-eclipse_jdt

	hg log -r "! author(r'B2G Bumper Bot')" --template "{desc} ;#; {author|person} ;#; {author|email}\n" > commitLogs-mozilla_firefox

***

How to run the program?

The Java application is provided both via source code and a runnable JAR file. Three command line arguments should be provided:

1. A five-digits binary code called db-config-code, where the n-th digit is 1 if the n-th part of the dataset should be created (0 otherwise). E.g., the code 00000 does not do anything, whereas the code 11111 will create all parts.

Note that the current version of the prototype does not create part 5. That part needs to be created manually.

2. The name of the project: e.g., eclipse_jdt, mozilla_firefox, etc.

3. The URL of the Bugzilla issue tracking system (the XML-RPC interface), e.g., https://bugs.eclipse.org/bugs/xmlrpc.cgi, https://bugzilla.mozilla.org/xmlrpc.cgi, etc.

Note that the current version does not support other issues tracking systems such as Jira.

***

How to create Part-1:

Note assumptions 1 and 2 (above). In a terminal run:

java -jar RecoDev-DataCollection.jar 10000 eclipse_jdt https://bugs.eclipse.org/bugs/xmlrpc.cgi

java -jar RecoDev-DataCollection.jar 10000 mozilla_firefox https://bugzilla.mozilla.org/xmlrpc.cgi

Output: MySQL tables *_issues and *_issues_relation in the recodev database.

***

How to create Part-2:

Note assumptions 1 and 2 (above). In a terminal run:

java -jar RecoDev-DataCollection.jar 01000 eclipse_jdt https://bugs.eclipse.org/bugs/xmlrpc.cgi

java -jar RecoDev-DataCollection.jar 01000 mozilla_firefox https://bugzilla.mozilla.org/xmlrpc.cgi

Output: MySQL tables *_developers in the recodev database.

***

How to create Part-3:

Note assumptions 1, 2 and 3 (above). In a terminal run:

java -jar RecoDev-DataCollection.jar 00100 eclipse_jdt https://bugs.eclipse.org/bugs/xmlrpc.cgi

java -jar RecoDev-DataCollection.jar 00100 mozilla_firefox https://bugzilla.mozilla.org/xmlrpc.cgi

Output: A bunch of text files, one per each issue, in the directories issues-*.

***

How to create Part-4:

Note assumptions 1, 2, 3 and 4 (above). In a terminal run:

java -jar RecoDev-DataCollection.jar 00010 eclipse_jdt https://bugs.eclipse.org/bugs/xmlrpc.cgi

java -jar RecoDev-DataCollection.jar 00010 mozilla_firefox https://bugzilla.mozilla.org/xmlrpc.cgi

Output: MySQL tables *_commit_logs in the recodev database.

***

How to create Part-5:

The current version of the prototype does not support automated creation of this part of the dataset. Thus, one needs to manually search the public data on the web (e.g., on Google) for the user names and email addresses of the developers (found in the develoeprs MySQL tables) and find the URLs of their social networking profiles and/or personal blogs. If this data is collected in an Excel sheet similar to our "devs.ods", "devs.xls", or "devs.csv" files, then the provided Python script (RecoDev_AddWebURLs.py) can be used for adding the URLs to the entries of the corresponding developers in the developers MySQL tables (E.g., see the web_url column of the eclipse_jdt_developers MySQL table.).

