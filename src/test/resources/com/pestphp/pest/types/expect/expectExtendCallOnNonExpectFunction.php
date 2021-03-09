<?php

expect()->extend('toBeWithinRange', function ($min, $max) {
    return $this->toBeGreaterThanOrEqual($min)
        ->toBeLessThanOrEqual($max);
});

test('numeric ranges', function () {
    notExpect(100)->toBeWithinRange(90, 110)-><caret>;
});