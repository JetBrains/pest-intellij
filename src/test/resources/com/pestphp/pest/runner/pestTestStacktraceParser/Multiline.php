<?php

test("myTest", function () {
    foo();
});

function foo() {
    boo();
}

function boo() {
    expect(1)->toBe(2);
}