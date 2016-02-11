#!/bin/sh
#input argument:
#project_name


cp -rvf $1 "$1_new"
rm -rvf "$1_new/.svn"
for i in "$1_new/*";
do
	rm -rvf $i/.svn
done

mysql -u recodev --password=ahm20kdd16 -h localhost -e 'SELECT dev_user_name FROM eclipse_jdt_developers WHERE is_invalid=1' recodev| while read -r data
do
	rm -rvf "$1_new/$data"
done

for i in $1_new/*;
do 
	echo $(ls $i | wc -l); 
	if [ $(ls $i | wc -l) -lt 3 ]
		then rm -rvf $i;
	fi
done
