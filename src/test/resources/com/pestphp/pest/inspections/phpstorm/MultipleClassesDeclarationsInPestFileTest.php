<?php

test('example', function () {
    expect(new TestingClass())
        ->toBeInstanceOf(TestingClass::class);
});

class TestingClass {
    //
}

class AnotherTestingClass {
    //
}