#!/bin/bash

ACTION=${1}

if [[ ${ACTION} == "start" ]]  
	then 
		#java -jar devops-0.0.1-SNAPSHOT.war --spring-profiles.active=dev &  
		echo "Action was $staaaaaaart"
elif [[ ${ACTION} == "stop" ]]
	then 
		#ps -fea | grep "this is just a-12 test.txt" | grep -v grep | awk '{print $2}' | xargs ls 
		echo "Action was stooooooooop"
elif [[ ${ACTION} == "status" ]] 
	 then 
		echo "Action was statttttttus"
else 
	echo "Unknown action received"
fi
