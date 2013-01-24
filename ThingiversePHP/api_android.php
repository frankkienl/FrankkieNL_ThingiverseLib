<?php
//Send code back to app
header("Location: thingiversedemo://code?code=" . $_GET[code]);
exit;
?>