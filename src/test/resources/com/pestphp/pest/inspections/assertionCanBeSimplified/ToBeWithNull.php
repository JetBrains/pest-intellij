<?php

test('example', function () {
    expect(true)-><warning descr="Assertion 'toBe' can be simplified to 'toBeNull'">toBe</warning>(null);
    expect(true)-><warning descr="Assertion 'toBe' can be simplified to 'toBeNull'">toBe</warning>(null, "message");
    expect(true)-><warning descr="Assertion 'toBe' can be simplified to 'toBeNull'">toBe</warning>(expected: null, message: "message");
});