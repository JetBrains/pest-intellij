<?php

use App\User;

test("Can get user's name", function () {
   $user = new User();

   $this->assertEquals("Oliver Nybroe", $user->getName());
});

test("is pest developer", function () {
   $user = new User();

   $this->assertTrue($user->isPestDeveloper());
});

test("is pest developer check if not false", function () {
   $user = new User();

   $this->assertNotEquals(false, $user->isPestDeveloper());
});

test("incorrect is pest developer", function () {
   $user = new User();

   $this->assertNotEquals(false, $user->isPestDeveloper());
});