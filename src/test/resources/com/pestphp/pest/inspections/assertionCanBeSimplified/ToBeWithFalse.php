<?php

test('example', function () {
    expect(true)-><weak_warning descr="Assertion 'toBe' can be simplified to 'toBeFalse'">toBe</weak_warning>(false);
    expect(true)-><weak_warning descr="Assertion 'toBe' can be simplified to 'toBeFalse'">toBe</weak_warning>(false, "message");
    expect(true)-><weak_warning descr="Assertion 'toBe' can be simplified to 'toBeFalse'">toBe</weak_warning>(expected: false, message: "message");
});