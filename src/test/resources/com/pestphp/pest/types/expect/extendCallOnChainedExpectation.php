<?php

expect()->extend('toChained', function ($min, $max) {
    return $this->toBeGreaterThanOrEqual($min)
        ->toBeLessThanOrEqual($max);
});

test('numeric ranges', function () {
    expect(100)
        ->toBe(100)
        ->toChained(90, 110)-><caret>;
});