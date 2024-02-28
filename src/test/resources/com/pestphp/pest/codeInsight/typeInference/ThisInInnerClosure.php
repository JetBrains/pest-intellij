<?php

uses(T::class);

test('test', function () {
    expect(someFoo(fn() => <type value="\PHPUnit\Framework\TestCase|T">$this</type>->call()))->toBe("bar");
});

function someFoo($closure) {
    return $closure();
}

trait T {
    function foo() {
        return "bar";
    }
}