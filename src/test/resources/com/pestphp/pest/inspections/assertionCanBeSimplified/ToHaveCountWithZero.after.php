<?php

test('example', function () {
    expect([1])->toBeEmpty();
    expect([1])->toBeEmpty("message");
    expect([1])->toBeEmpty(message: "message");
});