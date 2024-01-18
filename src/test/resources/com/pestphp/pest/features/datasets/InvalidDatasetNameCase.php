<?php

test('test 1', function ($number) {
    expect($number)->toBe(1);
})->with('some_numbers');

dataset(<weak_warning descr="Words in Pest dataset names must be separated by spaces">'some_numbers'</weak_warning>, function () {
    return [
        1
    ];
});

test('test 2', function ($number) {
    expect($number)->toBe(2);
})->with('some_numbers');