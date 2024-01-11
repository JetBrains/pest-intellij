<?php

test('example', function () {
    expect(true)-><weak_warning descr="Assertion 'toBe' can be simplified to 'toBeTrue'">toBe</weak_warning>(true);
    expect(true)-><weak_warning descr="Assertion 'toBe' can be simplified to 'toBeTrue'">toBe</weak_warning>(true, "message");
    expect(true)-><weak_warning descr="Assertion 'toBe' can be simplified to 'toBeTrue'">toBe</weak_warning>(expected: true, message: "message");
    expect(true)-><weak_warning descr="Assertion 'toBe' can be simplified to 'toBeTrue'">toBe</weak_warning>(TrUe);
});