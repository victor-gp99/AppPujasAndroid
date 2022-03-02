<?php

require 'DBManager.php';

$usr = $_GET['usr'];
$pass = $_GET['pass'];

if($usr && $pass) {
	$db = new DBManager();
	$json = $db->login($usr, $pass);

	echo $json;
} else {
	die('{"code":403}');
}

?>