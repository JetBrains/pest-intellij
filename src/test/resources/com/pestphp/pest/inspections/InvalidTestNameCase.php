<?php

test(<weak_warning descr="Words in Pest test names should be separated by spaces">'basic_test'</weak_warning>, function () {
    $this->assertTrue(true);
});

it(<weak_warning descr="Words in Pest test names should be separated by spaces">'is_basic_test'</weak_warning>, function () {
    $this->assertTrue(true);
});

it(<weak_warning descr="Words in Pest test names should be separated by spaces">'is.basic_test'</weak_warning>, function () {
    $this->assertTrue(true);
});

it(<weak_warning descr="Words in Pest test names should be separated by spaces">'is::basic_test'</weak_warning>, function () {
    $this->assertTrue(true);
});

it(<weak_warning descr="Words in Pest test names should be separated by spaces">'isBasic._test2'</weak_warning>, function () {
    $this->assertTrue(true);
});