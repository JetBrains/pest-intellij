<?php

use App\User;

test("Can get user's name", function () {
   $user = new User();

   $this->asserEquals("Oliver Nybroe", $user->getName());
});