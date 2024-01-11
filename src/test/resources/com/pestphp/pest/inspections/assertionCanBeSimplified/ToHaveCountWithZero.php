<?php

test('example', function () {
    expect([1])-><weak_warning descr="Assertion 'toHaveCount' can be simplified to 'toBeEmpty'">toHaveCount</weak_warning>(0);
    expect([1])-><weak_warning descr="Assertion 'toHaveCount' can be simplified to 'toBeEmpty'">toHaveCount</weak_warning>(0, "message");
    expect([1])-><weak_warning descr="Assertion 'toHaveCount' can be simplified to 'toBeEmpty'">toHaveCount</weak_warning>(count: 0, message: "message");
});