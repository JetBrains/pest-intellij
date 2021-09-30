<?php

namespace App\Models;

class User {

}

expect()->extend('createUser', function (): User {
    return new User();
});