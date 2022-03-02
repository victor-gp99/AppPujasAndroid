<?php

require 'DBManager.php';

$db = new DBManager();
$json = $db->showAll();

echo $json;

?>