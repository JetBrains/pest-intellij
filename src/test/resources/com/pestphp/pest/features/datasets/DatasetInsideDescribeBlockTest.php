<?php

describe('my block', function() {
    test('is delicious', function() {
        expect(true)->toBe(true);
    });

    test('check valid', function ($number) {
        expect($number)->toBe(1);
    })->with('some_numbers');

    dataset('some_numbers', function () {
        return [
            1,
            2
        ];
    });
});
