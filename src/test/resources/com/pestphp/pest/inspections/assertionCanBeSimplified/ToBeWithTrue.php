<?php

test('example', function () {
    expect(true)-><warning descr="Assertion 'toBe' can be simplified to 'toBeTrue'">toBe</warning>(true);
    expect(true)-><warning descr="Assertion 'toBe' can be simplified to 'toBeTrue'">toBe</warning>(true, "message");
    expect(true)-><warning descr="Assertion 'toBe' can be simplified to 'toBeTrue'">toBe</warning>(expected: true, message: "message");
    expect(true)-><warning descr="Assertion 'toBe' can be simplified to 'toBeTrue'">toBe</warning>(TrUe);
});