#/bin/bash

#input argument 1:
#base_path
#input argument 2:
#project_name


cp -rvf $2_ww "$2_ww_new"
rm -rvf "$2_ww_new/.svn"
for i in $2_ww_new/*
do
	rm -rvf $i/.svn
done

for i in $1RecoDev-DatasetPreparation-Collaborative/$2_ww_new/*
do	
	tmp_name="$(basename -a $i)"
	del="\."
	echo $tmp_name
	number_of_occurrences=$(grep -o $del <<< $tmp_name | wc -l)
	echo $number_of_occurrences
	if [ $number_of_occurrences -eq 1 ]
		then name=$(basename -a $i | cut -d'.' -f1)
	fi
	if [ $number_of_occurrences -eq 2 ]
		then name=$(basename -a $i | cut -d'.' -f1,2)
	fi
	if [ $number_of_occurrences -eq 3 ]
		then name=$(basename -a $i | cut -d'.' -f1,2,3)
	fi
	if [ -e "$1RecoDev-DatasetPreparation-ContentBased/$2_new/$name" ]
		then echo "exists"
	else
		echo "does NOT exist"
		rm -rvf $i
	fi
	name=""
done
