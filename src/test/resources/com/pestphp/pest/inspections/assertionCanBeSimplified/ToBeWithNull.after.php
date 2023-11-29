<?php

test('example', function () {
    expect(true)->toBeNull();
    expect(true)->toBeNull("message");
    expect(true)->toBeNull(message: "message");
});