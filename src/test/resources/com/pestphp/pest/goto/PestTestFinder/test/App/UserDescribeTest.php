<?php

use App\User;

describe('User', function () {
    test("is pest developer in describe", function () {
       $user = new User();

       $this->assertTrue($user->isPestDeveloper());
    });
});
