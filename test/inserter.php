<?php

	
	require_once "init.php";
		$con=mysqli_connect($server_name,$mysql_userName,$mysql_pass,$dbname);

	$input=file_get_contents("php://input");
	$jsonObj=json_decode($input);

	$content=$jsonObj->{CONTENT};
	$questions=$jsonObj->{QUESTION};
	$options=$jsonObj->{OPT};
	$answer=$jsonObj->{ANSWER};
	$type=$jsonObj->{TYPE};
	$lv=$jsonObj->{LEVEL};

	//echo "TYPE ".$type;
	//echo "LEVEL ".$lv;

	//echo "CONTENT ".$content;

	//echo "QUESTION ".$questions;
	//echo "OPT ".$options;
	//echo "ANSWER ".$answer;
	$q=$con->insert_id;




	$con->close();




?>