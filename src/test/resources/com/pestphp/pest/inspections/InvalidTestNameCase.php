<?php

test(<weak_warning descr="Pest test names words must be space separated.">'basic_test'</weak_warning>, function () {
    $this->assertTrue(true);
});
