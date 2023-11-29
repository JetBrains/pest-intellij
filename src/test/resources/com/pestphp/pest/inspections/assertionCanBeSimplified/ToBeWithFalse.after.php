<?php

test('example', function () {
    expect(true)->toBeFalse();
    expect(true)->toBeFalse("message");
    expect(true)->toBeFalse(message: "message");
});