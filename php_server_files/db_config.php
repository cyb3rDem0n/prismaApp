<?php
$mysql_hostname = "localhost";
$mysql_user = "testmyapp";
$mysql_password = "";
$mysql_database = "my_testmyapp";

$db = mysql_connect($mysql_hostname, $mysql_user, $mysql_password) or die("Opps some thing went wrong");
mysql_select_db($mysql_database, $db) or die("Opps some thing went wrong");

?> 