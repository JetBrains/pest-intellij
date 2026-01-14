<?php

describe('my block', function() {
    test('check valid', function ($number) {
        expect($number)->toBe(1);
    })->with('<caret>');

    dataset('some_numbers', function () {
        return [1, 2];
    });
});
