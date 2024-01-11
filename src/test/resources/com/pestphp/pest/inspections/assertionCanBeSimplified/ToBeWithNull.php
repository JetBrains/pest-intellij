<?php

test('example', function () {
    expect(true)-><weak_warning descr="Assertion 'toBe' can be simplified to 'toBeNull'">toBe</weak_warning>(null);
    expect(true)-><weak_warning descr="Assertion 'toBe' can be simplified to 'toBeNull'">toBe</weak_warning>(null, "message");
    expect(true)-><weak_warning descr="Assertion 'toBe' can be simplified to 'toBeNull'">toBe</weak_warning>(expected: null, message: "message");
});