<?php

test('one', function () {
    expect(1)->toBeTwo(1);
});

test('another one', function () {
    expect(2)->toBeTwo(2, 'two');
});
