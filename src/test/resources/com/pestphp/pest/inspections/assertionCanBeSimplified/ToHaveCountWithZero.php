<?php

test('example', function () {
    expect([1])-><warning descr="Assertion 'toHaveCount' can be simplified to 'toBeEmpty'">toHaveCount</warning>(0);
    expect([1])-><warning descr="Assertion 'toHaveCount' can be simplified to 'toBeEmpty'">toHaveCount</warning>(0, "message");
    expect([1])-><warning descr="Assertion 'toHaveCount' can be simplified to 'toBeEmpty'">toHaveCount</warning>(count: 0, message: "message");
});