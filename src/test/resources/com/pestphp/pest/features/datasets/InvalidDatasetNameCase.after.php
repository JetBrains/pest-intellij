<?php

test('test 1', function ($number) {
    expect($number)->toBe(1);
})->with('some numbers');

dataset('some numbers', function () {
    return [
        1
    ];
});

test('test 2', function ($number) {
    expect($number)->toBe(2);
})->with('some numbers');