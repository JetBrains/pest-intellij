<?php

expect()->extend('someExtend', function ($min, $max) {
    return $this->toBeGreaterThanOrEqual($min)
        ->toBeLessThanOrEqual($max);
});

test('numeric ranges', function () {
    expect(100)->someExtend(90, 110)-><caret>;
});