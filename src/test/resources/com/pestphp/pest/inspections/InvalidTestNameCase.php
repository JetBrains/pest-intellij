<?php

test(<weak_warning descr="Pest test names words must be space separated.">'basic_test'</weak_warning>, function () {
    $this->assertTrue(true);
});

it(<weak_warning descr="Pest test names words must be space separated.">'is_basic_test'</weak_warning>, function () {
    $this->assertTrue(true);
});