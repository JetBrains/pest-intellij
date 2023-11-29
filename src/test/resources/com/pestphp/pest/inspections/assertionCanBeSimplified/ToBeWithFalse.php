<?php

test('example', function () {
    expect(true)-><warning descr="Assertion 'toBe' can be simplified to 'toBeFalse'">toBe</warning>(false);
    expect(true)-><warning descr="Assertion 'toBe' can be simplified to 'toBeFalse'">toBe</warning>(false, "message");
    expect(true)-><warning descr="Assertion 'toBe' can be simplified to 'toBeFalse'">toBe</warning>(expected: false, message: "message");
});