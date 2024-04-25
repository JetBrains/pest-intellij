<?php

test('One test', function () {
    $this->assertTrue(true);
});

test('Another test', function () {
    $this->assertTrue(true);
});

class A {
}

class B {
}

test(A::class, function () {
    $this->assertTrue(true);
});

test(B::class, function () {
    $this->assertTrue(true);
});

class C {
    const c = "something";
}

test(C::c, function () {
    $this->assertTrue(true);
});

test(C::c, function () {
    $this->assertTrue(true);
});