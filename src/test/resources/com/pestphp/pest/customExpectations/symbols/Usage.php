<?php

test('one', function () {
    expect(1)->toBe<caret>One(1);
});

test('another one', function () {
    expect(2)->toBeOne(2, 'two');
});
