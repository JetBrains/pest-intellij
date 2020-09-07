<?php

test("Can get user's name", function () {
   $user = new \App\User();

   $this->asserEquals("Oliver Nybroe", $user->getName());
});