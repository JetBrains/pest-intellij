<?php

test('basic test', function () {
    $this->assertTrue(true);
});

test('basic.test', function () {
    $this->assertTrue(true);
});

test('basic::test', function () {
    $this->assertTrue(true);
});
