<?php

test('example', function () {
    expect(true)->toBeTrue();
    expect(true)->toBeTrue("message");
    expect(true)->toBeTrue(message: "message");
    expect(true)->toBeTrue();
});